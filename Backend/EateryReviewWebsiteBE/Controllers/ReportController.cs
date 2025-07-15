using Microsoft.AspNetCore.Mvc;
using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using Microsoft.EntityFrameworkCore;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ReportController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public ReportController(EateryReviewDbContext context)
        {
            _context = context;
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

        // POST: api/report
        [HttpPost]
        public IActionResult SubmitReport([FromBody] ReportRequestModel request)
        {
            var report = new Report
            {
                BlogId = request.BlogId,
                ReportReasonId = request.ReportReasonId,
                ReportStatus = 1, 
                ReportTime = DateTime.Now,
                ReporterId = request.ReporterId
            };

            _context.Reports.Add(report);
            _context.SaveChanges();

            return Ok(new { message = "Report submitted successfully." });
        }
    }
}
