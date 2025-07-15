using BusinessObjects.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProfileController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;
        private readonly ILogger<ProfileController> _logger;


        public ProfileController(EateryReviewDbContext context, ILogger<ProfileController> logger)
        {
            _context = context;
            _logger = logger;
        }

        [HttpGet("getUser")]
        [Authorize]
        public async Task<IActionResult> GetUserProfile()
        {
            // Extract user ID from JWT claims
            var userIdClaim = User.FindFirst(ClaimTypes.NameIdentifier);
            _logger.LogInformation("[JWT] User ID Claim: {UserId}", userIdClaim?.Value);

            if (userIdClaim == null)
                return Unauthorized("User ID not found in token.");

            if (!int.TryParse(userIdClaim.Value, out int userId))
                return BadRequest("Invalid user ID in token.");

            // Get user from database
            var user = await _context.Users.Where(u => u.UserId == userId)
                    .Select(u => new
                    {
                        UserID = u.UserId,
                        u.Username,
                        Email = u.UserEmail,
                        DisplayName = u.DisplayName,
                        PhoneNumber = u.UserPhone,
                        Role = u.RoleId.ToString(),
                        UserImage = u.UserImage != null ? Convert.ToBase64String(u.UserImage) : null
                    })
                    .FirstOrDefaultAsync();

            _logger.LogInformation("[DB] Retrieved User id: {UserId}", user);

            if (user == null)
                return NotFound();

            return Ok(user);
        }

        // GET: api/Profile/5/blogs
        [HttpGet("{id}/blogs")]
        public async Task<IActionResult> GetUserBlogs(int id, int page = 1, int pageSize = 8)
        {
            var userExists = await _context.Users.AnyAsync(u => u.UserId == id);
            if (!userExists) return NotFound();

            var query = _context.Blogs
                .Where(b => b.UserId == id)
                .OrderByDescending(b => b.BlogDate);

            var totalCount = await query.CountAsync();
            var blogs = await query
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .Select(b => new
                {
                    b.BlogId,
                    b.BlogTitle,
                    b.BlogContent,
                    b.BlogDate,
                    b.BlogStatus,
                    Image = _context.BlogImages
                        .Where(bi => bi.BlogId == b.BlogId)
                        .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                        .FirstOrDefault()
                })
                .ToListAsync();

            return Ok(new
            {
                blogs,
                totalCount
            });
        }


        [HttpGet("{id}/bookmarks")]
        public async Task<IActionResult> GetBookmarks(int id, int page = 1, int pageSize = 8)
        {
            var query = _context.Bookmarks
                .Where(b => b.BookmarkByUserId == id);

            var totalCount = await query.CountAsync();

            var bookmarks = await query
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .Select(b => new
                {
                    b.Blog.BlogId,
                    b.Blog.BlogTitle,
                    Image = b.Blog.BlogBillImage != null
                        ? Convert.ToBase64String(b.Blog.BlogBillImage)
                        : null
                })
                .ToListAsync();

            return Ok(new
            {
                bookmarks,
                totalCount
            });
        }


        //[HttpGet("{id}/notifications")]
        //public async Task<IActionResult> GetNotifications(int id)
        //{
        //    var notifications = await _context.Notifications
        //        .Where(n => n.UserId == id)
        //        .Select(n => new {
        //            n.Id,
        //            n.Title,
        //            n.Message,
        //            Timestamp = n.Timestamp.ToString("yyyy-MM-dd HH:mm:ss")
        //        }).ToListAsync();

        //    return Ok(notifications);
        //}

        // GET: api/Profile/5
        [HttpGet("{id}")]
        public async Task<ActionResult<User>> GetUser(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null)
            {
                return NotFound();
            }
            return Ok(user);
        }

        [HttpPost("UpdateProfile")]
        public async Task<IActionResult> UpdateProfile([FromBody] UpdateProfileModel model)
        {
            var user = await _context.Users.FindAsync(model.UserId);
            if (user == null)
                return NotFound("User not found.");

            user.DisplayName = model.DisplayName;
            user.UserPhone = model.Phone;

            // ImageLink should be base64 or URL sent as a string in JSON
            if (model.ImageLink != null && model.ImageLink.Length > 0)
            {
                user.UserImage = await ConvertToBytesAsync(model.ImageLink);
            }

            await _context.SaveChangesAsync();

            return Ok();
        }

        private bool UserExists(int id)
        {
            return _context.Users.Any(e => e.UserId == id);
        }

        [HttpGet("isOwner/{userId}/{curUserId}")]
        public IActionResult IsOwner(int userId, int curUserId)
        {
            if (userId <= 0 || curUserId <= 0)
            {
                return BadRequest("Invalid user IDs provided.");
            }

            return Ok(new { isOwner = userId == curUserId });
        }

        public class UpdateProfileModel
        {
            public int UserId { get; set; }
            public string DisplayName { get; set; }
            public string Phone { get; set; }
            public string? ImageLink { get; set; }
            // Add other fields as needed
        }

        private Task<byte[]> ConvertToBytesAsync(string base64String)
        {
            return Task.FromResult(Convert.FromBase64String(base64String));
        }

        [HttpPost("add-bookmark")]
        public async Task<IActionResult> AddBookmark([FromQuery] int userId, [FromQuery] int blogId)
        {
            if (userId <= 0 || blogId <= 0)
                return BadRequest("Invalid user or blog ID.");

            var userExists = await _context.Users.AnyAsync(u => u.UserId == userId);
            var blogExists = await _context.Blogs.AnyAsync(b => b.BlogId == blogId);

            if (!userExists || !blogExists)
                return NotFound("User or Blog not found.");

            var existing = await _context.Bookmarks
                .FirstOrDefaultAsync(b => b.BookmarkByUserId == userId && b.BlogId == blogId);

            if (existing != null)
                return BadRequest("Blog already bookmarked.");

            var bookmark = new Bookmark
            {
                BookmarkByUserId = userId,
                BlogId = blogId
            };

            _context.Bookmarks.Add(bookmark);
            await _context.SaveChangesAsync();

            return Ok(new { message = "Bookmark added successfully." });
        }

        [HttpDelete("remove-bookmark")]
        public async Task<IActionResult> RemoveBookmark([FromQuery] int userId, [FromQuery] int blogId)
        {
            if (userId <= 0 || blogId <= 0)
                return BadRequest("Invalid user or blog ID.");
            var bookmark = await _context.Bookmarks
                .FirstOrDefaultAsync(b => b.BookmarkByUserId == userId && b.BlogId == blogId);
            if (bookmark == null)
                return NotFound("Bookmark not found.");
            _context.Bookmarks.Remove(bookmark);
            await _context.SaveChangesAsync();
            return Ok(new { message = "Bookmark removed successfully." });
        }



        [HttpGet("{userId}/notifications")]
        public async Task<IActionResult> GetNotifications(int userId)
        {
            var notifications = await _context.Notifications
                .Where(n => n.UserId == userId)
                .OrderByDescending(n => n.Timestamp)
                .ToListAsync();

            return Ok(notifications);
        }

        [HttpPost("{userId}/notifications/read-all")]
        public async Task<IActionResult> MarkAllAsRead(int userId)
        {
            var unread = await _context.Notifications
                .Where(n => n.UserId == userId && !n.IsRead)
                .ToListAsync();

            foreach (var n in unread)
                n.IsRead = true;

            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}
