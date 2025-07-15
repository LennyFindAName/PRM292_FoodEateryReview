using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Security.Cryptography;
using System.Text.Json;
using System.Text;
using System;
using BusinessObjects.Models;
using BusinessObjects.Enums;
using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PayOSController : ControllerBase
    {
        private readonly HttpClient _httpClient;
        private readonly string clientId;
        private readonly string apiKey;
        private readonly string checksumKey;
        private readonly EateryReviewDbContext _context;

        public PayOSController(IConfiguration _config, EateryReviewDbContext context)
        {
            clientId = _config["PayOS:ClientId"];
            apiKey = _config["PayOS:ApiKey"];
            checksumKey = _config["PayOS:ChecksumKey"];

            _context = context;
            _httpClient = new HttpClient();
            _httpClient.DefaultRequestHeaders.Add("x-client-id", clientId);
            _httpClient.DefaultRequestHeaders.Add("x-api-key", apiKey);
        }
        [HttpGet("verify/{orderCode}")]
        [AllowAnonymous]
        public async Task<IActionResult> VerifyOrderCode(string orderCode)
        {
            var transaction = await _context.WalletTransactions
                .Include(t => t.User)
                .FirstOrDefaultAsync(t => t.OrderCode == orderCode);

            if (transaction == null) return NotFound("Không tìm thấy giao dịch");

            if (transaction.TransactionStatus == WalletTransactionStatus.Success.ToString())
                return Ok("Đã cập nhật");

            // Nếu vẫn đang pending, ta cập nhật luôn (nhớ kiểm tra với PayOS API nếu cần xác thực thêm)
            transaction.TransactionStatus = WalletTransactionStatus.Success.ToString();
            transaction.TransactionNote = "Cập nhật từ redirect sau thanh toán";
            transaction.User.WalletBalance += transaction.Amount;

            await _context.SaveChangesAsync();
            return Ok("Cập nhật thành công");
        }

        [Authorize]
        [HttpPost("create-payment")]
        public async Task<IActionResult> CreatePayment([FromBody] PaymentRequest req)
        {
            // Lấy userId từ token hoặc gán tạm
            int userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);

            // Tạo orderCode là số, đảm bảo < 9007199254740991
            long timestamp = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
            long orderCode = userId * 1_000_000_0000 + timestamp; // số 11-12 chữ số, an toàn

            // Lưu vào DB dưới dạng chuỗi nếu muốn
            var transaction = new WalletTransaction
            {
                UserId = userId,
                Amount = req.amount,
                OrderCode = orderCode.ToString(),
                TransactionStatus = WalletTransactionStatus.Pending.ToString(),
                TransactionNote = "Chờ thanh toán",
                TransactionDate = DateTime.Now
            };
            _context.WalletTransactions.Add(transaction);
            await _context.SaveChangesAsync();

            // Gửi tới PayOS – orderCode là số
            var body = new
            {
                orderCode,
                amount = req.amount,
                description = "Nạp tiền vào ví",
                cancelUrl = $"http://localhost:5143/wallet/result?status=cancel&orderCode={orderCode}",
                returnUrl = $"http://localhost:5143/wallet/result?status=success&orderCode={orderCode}"
            };

            string raw = $"amount={body.amount}&cancelUrl={body.cancelUrl}&description={body.description}&orderCode={body.orderCode}&returnUrl={body.returnUrl}";
            string signature = ComputeSignature(raw, checksumKey);

            var payload = new
            {
                body.orderCode,
                body.amount,
                body.description,
                body.cancelUrl,
                body.returnUrl,
                signature
            };

            var resp = await _httpClient.PostAsync("https://api-merchant.payos.vn/v2/payment-requests",
                new StringContent(JsonSerializer.Serialize(payload), Encoding.UTF8, "application/json"));
            var txt = await resp.Content.ReadAsStringAsync();

            return Content(txt, "application/json");
        }

        [HttpPost("webhook")]
        [AllowAnonymous]
        public async Task<IActionResult> Webhook([FromBody] JsonElement payload)
        {
            var dataRaw = payload.GetProperty("data").ToString();
            var signature = payload.GetProperty("signature").GetString();
            string computedSig = ComputeSignature(dataRaw, checksumKey);
            if (computedSig != signature)
                return BadRequest("Invalid signature");

            var data = JsonDocument.Parse(dataRaw).RootElement;
            var orderCode = data.GetProperty("orderCode").GetString();

            var transaction = await _context.WalletTransactions.FirstOrDefaultAsync(t => t.OrderCode == orderCode);
            if (transaction == null) return NotFound("Không tìm thấy giao dịch");

            if (transaction.TransactionStatus == WalletTransactionStatus.Success.ToString())
                return Ok("Đã xử lý trước đó");

            transaction.TransactionStatus = WalletTransactionStatus.Success.ToString();
            transaction.TransactionNote = "Giao dịch thành công";

            var user = await _context.Users.FindAsync(transaction.UserId);
            if (user != null)
                user.WalletBalance += transaction.Amount;

            await _context.SaveChangesAsync();
            return Ok("Webhook received");
        }

        [HttpGet("balance")]
        [Authorize]

        public IActionResult GetBalance()
        {
            int userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);
            var user = _context.Users.FirstOrDefault(u => u.UserId == userId);
            return Ok(new { balance = user.WalletBalance });
        }

        [HttpGet("history")]
        [Authorize]
        public IActionResult GetHistory()
        {
            int userId = int.Parse(User.FindFirst(ClaimTypes.NameIdentifier)!.Value);
            var list = _context.WalletTransactions
                        .Where(t => t.UserId == userId)
                        .OrderByDescending(t => t.TransactionDate)
                        .Select(t => new
                        {
                            t.TransactionDate,
                            t.Amount,
                            transactionStatus = t.TransactionStatus
                        })
                        .ToList();
            return Ok(list);
        }

        private string ComputeSignature(string raw, string key)
        {
            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(key));
            return BitConverter.ToString(hmac.ComputeHash(Encoding.UTF8.GetBytes(raw)))
                .Replace("-", "").ToLower();
        }
    }

    public class PaymentRequest
    {
        public int amount { get; set; }
    }
}
