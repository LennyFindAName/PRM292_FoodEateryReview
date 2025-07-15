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
    }
}
