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
    public class ReplyController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public ReplyController(EateryReviewDbContext context)
        {
            _context = context;
        }

        // GET: api/reply/comment/5
        [HttpGet("comment/{commentId}")]
        public IActionResult GetRepliesForComment(int commentId)
        {
            var replies = _context.Replies
                .Include(r => r.User)
                .Where(r => r.CommentId == commentId)
                .OrderByDescending(r => r.ReplyDate)
                .Select(r => new
                {
                    r.ReplyId,
                    r.Content,
                    r.ReplyDate,
                    r.ReplyLike,
                    r.UserId, // Return user ID
                    Username = r.User != null ? r.User.DisplayName : "Anonymous"
                })
                .ToList();

            return Ok(replies);
        }

        // POST: api/reply
        [HttpPost]
        public IActionResult PostReply([FromBody] ReplyRequestModel request)
        {
            var reply = new Reply
            {
                Content = request.Content,
                CommentId = request.CommentId,
                UserId = request.UserId,
                ReplyDate = DateTime.Now,
                ReplyStatus = 1,
                ReplyLike = 0
            };

            _context.Replies.Add(reply);
            _context.SaveChanges();

            return Ok(new
            {
                reply.ReplyId,
                reply.Content,
                reply.ReplyDate,
                reply.ReplyLike,
                reply.UserId
            });
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteReply(int id)
        {
            var reply = _context.Replies.Find(id);
            if (reply == null) return NotFound();

            // Delete ReplyLikes for this reply
            var replyLikes = _context.ReplyLikes.Where(rl => rl.ReplyId == id);
            _context.ReplyLikes.RemoveRange(replyLikes);

            // Delete the reply
            _context.Replies.Remove(reply);
            _context.SaveChanges();

            return NoContent();
        }

        [HttpPost("like/{replyId}")]
        public IActionResult LikeReply(int replyId, [FromBody] int userId)
        {
            var existingLike = _context.ReplyLikes.FirstOrDefault(rl => rl.ReplyId == replyId && rl.UserId == userId);
            if (existingLike != null)
            {
                _context.ReplyLikes.Remove(existingLike);
                var reply = _context.Replies.FirstOrDefault(r => r.ReplyId == replyId);
                if (reply != null) reply.ReplyLike--;
            }
            else
            {
                _context.ReplyLikes.Add(new ReplyLike { ReplyId = replyId, UserId = userId });
                var reply = _context.Replies.FirstOrDefault(r => r.ReplyId == replyId);
                if (reply != null) reply.ReplyLike++;
            }

            _context.SaveChanges();
            return Ok();
        }

    }
}
