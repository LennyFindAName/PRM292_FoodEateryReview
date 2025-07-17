using BusinessObjects.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AdminController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public AdminController(EateryReviewDbContext context)
        {
            _context = context;
        }

        // GET: api/Admin/dashboard
        [HttpGet("dashboard")]
        public IActionResult GetDashboardData(int page = 1, int pageSize = 10)
        {
            var today = DateOnly.FromDateTime(DateTime.Today);

            var blogsQuery = _context.Blogs
                .OrderByDescending(b => b.BlogDate)
                .Select(b => new
                {
                    b.BlogId,
                    b.BlogTitle,
                    b.BlogDate,
                    b.BlogRate,
                    b.BlogLike,
                    b.BlogStatus,
                    b.EateryAddressDetail
                });

            var pagedBlogs = blogsQuery
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .ToList();

            var result = new
            {
                TotalUsers = _context.Users.Count(),
                ActiveUsersToday = _context.Users.Count(u => u.LastLogin.HasValue && u.LastLogin.Value.Date == DateTime.Today),
                TotalBlogs = _context.Blogs.Count(),
                NewBlogsToday = _context.Blogs.Count(b => b.BlogDate == today),
                Blogs = pagedBlogs,
                TotalCount = blogsQuery.Count()
            };

            return Ok(result);
        }


        // GET: api/Admin/moderators
        [HttpGet("moderators")]
        public IActionResult GetModerators()
        {
            var moderators = _context.Users
                .Where(u => u.RoleId == 2) // Assuming RoleId 2 is for Moderators
                .Select(u => new
                {
                    u.UserId,
                    u.UserEmail,
                    u.Username,
                    u.DisplayName,
                    u.UserStatus,
                    u.LastLogin
                })
                .ToList();
            return Ok(moderators);
        }

        // POST: api/Admin/toggle-user-status/{id}
        [HttpPost("toggle-user-status/{id}")]
        public async Task<IActionResult> ToggleUserStatus(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return NotFound();

            user.UserStatus = user.UserStatus == 1 ? 0 : 1;
            await _context.SaveChangesAsync();

            return Ok(new { success = true, newStatus = user.UserStatus });
        }

        // POST: api/Admin/toggle-blog-status/{id}
        [HttpPost("toggle-blog-status/{id}")]
        public async Task<IActionResult> ToggleBlogStatus(int id)
        {
            var blog = await _context.Blogs.FindAsync(id);
            if (blog == null) return NotFound();

            blog.BlogStatus = blog.BlogStatus == 1 ? 0 : 1;
            await _context.SaveChangesAsync();

            return Ok(new { success = true, newStatus = blog.BlogStatus });
        }

        // POST: api/Admin/add-moderator
        [HttpPost("add-moderator")]
        public async Task<IActionResult> AddModerator([FromForm] AddModeratorModel model)
        {
            if (!ModelState.IsValid)
                return BadRequest("Please fill in all required fields.");

            if (_context.Users.Any(u => u.Username == model.Username || u.UserEmail == model.UserEmail))
                return BadRequest("Username or email already exists.");

            var newUser = new User
            {
                Username = model.Username,
                DisplayName = model.DisplayName,
                UserEmail = model.UserEmail,
                Password = BCrypt.Net.BCrypt.HashPassword(model.Password),
                RoleId = 2,
                UserStatus = 1,
                LastLogin = null
            };

            _context.Users.Add(newUser);
            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpPut("edit-moderator")]
        public async Task<IActionResult> EditModerator([FromForm] EditModeratorModel model)
        {
            var user = await _context.Users.FindAsync(model.UserId);
            if (user == null) return NotFound("User not found");

            user.Username = model.Username;
            user.DisplayName = model.DisplayName;
            user.UserEmail = model.UserEmail;

            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}