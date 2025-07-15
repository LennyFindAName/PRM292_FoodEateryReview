using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Linq;
using System.Text;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CommentController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public CommentController(EateryReviewDbContext context)
        {
            _context = context;
        }

        // GET: api/comment/blog/5
        [HttpGet("blog/{blogId}")]
        public IActionResult GetCommentsForBlog(int blogId)
        {
            var comments = _context.Comments
                .Where(c => c.BlogId == blogId)
                .Include(c => c.User)
                .Include(c => c.Replies)
                    .ThenInclude(r => r.User) // now valid!
                .OrderByDescending(c => c.CommentDate)
                .AsEnumerable()
                .Select(c => new
                {
                    c.CommentId,
                    c.Content,
                    c.CommentDate,
                    c.CommentLike,
                    c.UserId,
                    Username = c.User?.DisplayName ?? "Anonymous",
                    Replies = c.Replies
                        .OrderByDescending(r => r.ReplyDate)
                        .Select(r => new
                        {
                            r.ReplyId,
                            r.Content,
                            r.ReplyDate,
                            r.ReplyLike,
                            r.UserId,
                            Username = r.User?.DisplayName ?? "Anonymous"
                        })
                })
                .ToList();

            return Ok(comments);
        }


        // POST: api/comment
        [HttpPost]
        public IActionResult PostComment([FromBody] CommentRequestModel request)
        {
            var comment = new Comment
            {
                Content = request.Content,
                BlogId = request.BlogId,
                UserId = request.UserId,
                CommentDate = DateTime.Now,
                CommentStatus = 1,
                CommentLike = 0
            };

            _context.Comments.Add(comment);
            _context.SaveChanges();

            return Ok(new
            {
                comment.CommentId,
                comment.Content,
                comment.CommentDate,
                comment.CommentLike
            });
        }

        // DELETE: api/comment/5
        [HttpDelete("{id}")]
        public IActionResult DeleteComment(int id)
        {
            var comment = _context.Comments
                .Include(c => c.Replies)
                .FirstOrDefault(c => c.CommentId == id);

            if (comment == null)
            {
                return NotFound();
            }

            // Delete ReplyLikes associated with each reply
            var replyIds = comment.Replies.Select(r => r.ReplyId).ToList();
            var replyLikesToDelete = _context.ReplyLikes.Where(rl => replyIds.Contains((int)rl.ReplyId));
            _context.ReplyLikes.RemoveRange(replyLikesToDelete);

            // Delete the replies
            _context.Replies.RemoveRange(comment.Replies);

            // Delete CommentLikes
            var commentLikes = _context.CommentLikes.Where(cl => cl.CommentId == comment.CommentId);
            _context.CommentLikes.RemoveRange(commentLikes);

            // Delete the comment
            _context.Comments.Remove(comment);
            _context.SaveChanges();

            return NoContent();
        }

        [HttpPost("like/{commentId}")]
        public IActionResult LikeComment(int commentId, [FromBody] int userId)
        {
            var existingLike = _context.CommentLikes.FirstOrDefault(cl => cl.CommentId == commentId && cl.UserId == userId);
            if (existingLike != null)
            {
                _context.CommentLikes.Remove(existingLike);
                var comment = _context.Comments.FirstOrDefault(c => c.CommentId == commentId);
                if (comment != null) comment.CommentLike--;
            }
            else
            {
                _context.CommentLikes.Add(new CommentLike { CommentId = commentId, UserId = userId });
                var comment = _context.Comments.FirstOrDefault(c => c.CommentId == commentId);
                if (comment != null) comment.CommentLike++;
            }

            _context.SaveChanges();
            return Ok();
        }



    }
}
