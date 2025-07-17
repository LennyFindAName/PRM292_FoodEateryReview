using Azure;
using BusinessObjects.Models;
using BusinessObjects.ModerationModels.Blog;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Services.Helper;
using Services.Response;
using System.Text;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ReportController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;
        private readonly GeminiHelper _geminiHelper;

        public ReportController(EateryReviewDbContext context, GeminiHelper geminiHelper)
        {
            _context = context;
            _geminiHelper = geminiHelper;
        }

        // GET: api/report/reasons
        [HttpGet("reasons")]
        public IActionResult GetReportReasons()
        {
            var reasons = _context.ReportReasons
                .Select(r => new
                {
                    r.ReasonId,
                    r.BlogReasonContent
                })
                .ToList();

            return Ok(reasons);
        }

        [HttpPost]
        public async Task<IActionResult> SubmitReport([FromBody] ReportRequestModel request)
        {
            var report = new Report
            {
                BlogId = request.BlogId,
                ReportReasonId = request.ReportReasonId,
                ReportTime = DateTime.Now,
                ReporterId = request.ReporterId,
                ReportContent = request.ReportContent
            };

            _context.Reports.Add(report);
            await _context.SaveChangesAsync();

            // Count reports for this blog
            var reportCount = await _context.Reports.CountAsync(r => r.BlogId == request.BlogId);

            if (reportCount % 5 == 0)
            {
                // Get the latest 5 reports for this blog
                var latestReports = await _context.Reports
                    .Where(r => r.BlogId == request.BlogId)
                    .OrderByDescending(r => r.ReportTime)
                    .Take(5)
                    .Include(r => r.ReportReason)
                    .ToListAsync();

                // Get the blog and user
                var blog = await _context.Blogs
                    .Include(b => b.User)
                    .FirstOrDefaultAsync(b => b.BlogId == request.BlogId);

                if (blog != null)
                {
                    await AssessBlogByAIWithReportsAsync(blog, latestReports);
                }
            }
            return Ok(new { message = "Report submitted successfully." });
        }

        [HttpGet("getReports")]
        public IActionResult GetReports(int blogId)
        {
            var reports = _context.Reports
                .Where(r => r.BlogId == blogId)
                .Include(r => r.ReportReason)
                .OrderByDescending(r => r.ReportTime)
                .Select(r => new
                {
                    r.ReportId,
                    r.ReportContent,
                    r.ReportTime,
                    r.ReporterId,
                    Reason = r.ReportReason != null ? r.ReportReason.BlogReasonContent : null
                })
                .ToList();

            return Ok(reports);
        }

        private async Task AssessBlogByAIWithReportsAsync(Blog blog, List<Report> latestReports)
        {
            try
            {
                var prompt = $@"
                Bài viết bị báo cáo nhiều lần. Dưới đây là nội dung bài viết và 5 báo cáo gần nhất:
                ---
                Tiêu đề: {blog.BlogTitle}
                Nội dung: {blog.BlogContent}
                Người đăng: {blog.User?.DisplayName}
                Ngày đăng: {blog.BlogDate}
                ---
                Các báo cáo gần nhất:
                {string.Join("\n", latestReports.Select((r, i) => $"[{i + 1}] Lý do: {r.ReportReason.BlogReasonContent}" +
                $"\nNội dung báo cáo: {r.ReportContent}\nNgười báo cáo: {r.ReporterId}\nThời gian: {r.ReportTime}"))}
            
                Hãy đánh giá bài viết này dựa trên nội dung và các báo cáo trên.

                Nếu nội dung bài viết rõ ràng vi phạm (ví dụ: chứa ngôn từ thù ghét, quảng bá chất cấm, sai sự thật nguy hiểm)
                , và trùng khớp với ít nhất một báo cáo → REJECT.
                Nếu báo cáo nêu ra cáo buộc hợp lý, nhưng nội dung bài viết không thể hiện vi phạm rõ ràng, hoặc chưa đủ chứng cứ để xác minh → PENDING.
                Nếu tất cả các báo cáo đều nghiêm trọng nhưng không đưa ra bằng chứng, hoặc bằng chứng không hề liên quan đến bài viết
                , và nội dung bài viết không cho thấy bất kỳ dấu hiệu vi phạm nào,
                hãy -> APPROVE. Không cần chuyển sang PENDING nếu không có cơ sở kiểm chứng hoặc nghi vấn cụ thể trong nội dung.
                Chỉ REJECT khi vi phạm được thể hiện rõ trong nội dung, không được suy diễn hoặc giả định nếu không có bằng chứng trong bài viết.

                Trả lời bắt đầu bằng 'APPROVE' nếu hợp lệ, tương tự với REJECT và PENDING,
                Kèm lý do.
                Không format.
                ";
                var aiResponse = await _geminiHelper.GetChatResponseAsync(prompt);
                var trimmed = aiResponse.Trim();
                if (trimmed.StartsWith("APPROVE", StringComparison.OrdinalIgnoreCase))
                {
                    blog.BlogStatus = (int)BlogModerationStatus.Approved;
                    blog.Opinion = trimmed.Substring(7).TrimStart(':', '-', ' ', '.');
                }
                else if (trimmed.StartsWith("REJECT", StringComparison.OrdinalIgnoreCase))
                {
                    blog.BlogStatus = (int)BlogModerationStatus.Rejected;
                    blog.Opinion = trimmed.Substring(6).TrimStart(':', '-', ' ', '.');
                }
                else if (trimmed.StartsWith("PENDING", StringComparison.OrdinalIgnoreCase))
                {
                    blog.BlogStatus = (int)BlogModerationStatus.Pending;
                    blog.Opinion = trimmed.Substring(7).TrimStart(':', '-', ' ', '.');
                }
                else
                {
                    blog.BlogStatus = (int)BlogModerationStatus.Pending;
                    blog.Opinion = trimmed.Substring(7).TrimStart(':', '-', ' ', '.');
                }
                await _context.SaveChangesAsync();

            }
            catch (Exception ex)
            {
                blog.Opinion = $"Error assessing by AI: {ex.Message}";
                blog.BlogStatus = (int)BlogModerationStatus.Pending;
                await _context.SaveChangesAsync();
            }
        }
    }
}