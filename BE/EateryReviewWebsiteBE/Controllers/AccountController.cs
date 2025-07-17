using Microsoft.AspNetCore.Mvc;
using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using BusinessObjects.ResponseModels;


namespace EateryReviewWebsiteBE.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AccountController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public AccountController(EateryReviewDbContext context)
        {
            _context = context ?? throw new ArgumentNullException(nameof(context));
        }

        [HttpGet]
        public IActionResult GetUserById(int id)
        {
            var user = _context.Users.FirstOrDefault(u => u.UserId == id);
            try
            {
                if (user != null)
                {
                    var userResponse = new AccountResponseModel
                    {
                        UserId = user.UserId,
                        DisplayName = user.DisplayName,
                        Username = user.Username,
                        UserEmail = user.UserEmail,
                        UserImage = user.UserImage,
                        UserPhone = user.UserPhone,
                        RoleId = user.RoleId,
                        UserStatus = user.UserStatus
                    };
                    return Ok(userResponse);
                }
                else
                {
                    return NotFound("User not found.");
                }
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, $"Error retrieving user: {ex.Message}");
            }
        }

        [HttpGet("searchUsers")]
        public IActionResult GetSearchedPagedUsers(string result = "", int page = 1, int pageSize = 10)
        {
            var skip = (page - 1) * pageSize;

            // Start with all users and apply search filter
            var query = _context.Users.AsQueryable();

            // Apply search filter if result parameter is provided
            if (!string.IsNullOrEmpty(result))
            {
                query = query.Where(u => u.Username.Contains(result) || u.DisplayName.Contains(result));
            }

            // Get total count of filtered results
            var totalCount = query.Count();

            // Apply ordering, pagination, and projection
            var users = query
                .OrderBy(u => u.DisplayName)
                .Skip(skip)
                .Take(pageSize)
                .Select(user => new
                {
                    user.UserId,
                    user.DisplayName,
                    user.Username,
                    user.UserEmail,
                    UserImage = user.UserImage != null ? Convert.ToBase64String(user.UserImage) : null,
                    user.RoleId,
                    user.UserStatus
                })
                .ToList();

            return Ok(new { totalCount, users });
        }
    }
}
