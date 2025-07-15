using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Caching.Memory;
using Services.Helper;
using System;
using System.Security.Claims;

namespace EateryReviewWebsiteBE.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ChatController : ControllerBase
    {
        private readonly GeminiHelper _geminiService;
        private readonly IMemoryCache _cache;

        public ChatController(GeminiHelper geminiService, IMemoryCache cache)
        {
            _cache = cache;
            _geminiService = geminiService ?? throw new ArgumentNullException(nameof(geminiService));
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> SendMessage([FromBody] ChatRequest request)
        {
            if (string.IsNullOrEmpty(request.Message))
                return BadRequest(new { Response = "Message cannot be empty." });

            var response = await _geminiService.GetChatResponseAsync(request.Message);

            // Lấy userId từ token
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            // Trường hợp bạn dùng custom claim name:
            // var userId = User.FindFirst("userId")?.Value;

            if (string.IsNullOrEmpty(userId))
                return Unauthorized(new { Response = "Invalid token: userId not found" });

            var cacheKey = $"chat_{userId}";

            // Lấy hoặc khởi tạo lịch sử chat từ cache
            var chatList = _cache.GetOrCreate(cacheKey, entry =>
            {
                entry.SlidingExpiration = TimeSpan.FromMinutes(60);
                return new List<ChatHistoryModel>();
            });

            // Thêm message mới vào lịch sử
            chatList.Add(new ChatHistoryModel
            {
                UserId = userId,
                Message = request.Message,
                Response = response,
                Timestamp = DateTime.UtcNow
            });

            // Cập nhật cache
            _cache.Set(cacheKey, chatList);

            return Ok(new { Response = response });
        }
        [HttpGet("history")]
        [Authorize]
        public IActionResult GetChatHistory()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (string.IsNullOrEmpty(userId))
                return Unauthorized(new { Response = "Invalid token: userId not found" });

            var cacheKey = $"chat_{userId}";
            if (_cache.TryGetValue(cacheKey, out List<ChatHistoryModel> chatList))
            {
                return Ok(chatList.OrderByDescending(c => c.Timestamp));
            }

            return Ok(new List<ChatHistoryModel>());
        }
        [HttpPost("clear-chat")]
        [Authorize]
        public IActionResult Logout()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;

            if (string.IsNullOrEmpty(userId))
                return Unauthorized(new { Response = "Invalid token: userId not found" });

            _cache.Remove($"chat_{userId}"); // Xóa lịch sử chat cache

            return Ok(new { Message = "User logged out and chat cache cleared." });
        }


    }
    public class ChatRequest
    {
        public string Message { get; set; } = string.Empty;
    }
}
