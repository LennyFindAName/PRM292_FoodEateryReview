using BusinessObjects.RequestModels.Authen;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Services.Services.AuthenService;
using System.Security.Claims;

namespace EateryReviewWebsiteBE.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthenController : ControllerBase
    {
        private readonly IAuthenService _authenService;

        public AuthenController(IAuthenService authenService)
        {
            _authenService = authenService;
        }
        [HttpPost("login")]
        [ProducesResponseType(typeof(LoginRequestModel), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> Login([FromBody] LoginRequestModel model)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);
            try
            {
                var response = await _authenService.LoginAsync(model);
                if (response == null)
                    return Unauthorized("Invalid username or password.");
                return Ok(response);
            }
            catch (Exception ex)
            {
                return BadRequest($"Login failed: {ex.Message}");
            }
        }

        [HttpPost("register")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status409Conflict)]
        public async Task<IActionResult> Register([FromBody] RegisterRequestModel model)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try
            {
                await _authenService.RegisterAsync(model);
                return Ok("Registration successful.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Registration failed: {ex.Message}");
            }
        }


        [HttpPost("forgot-password")]
        public async Task<IActionResult> ForgotPassword([FromBody] ForgotPasswordRequestModel model)
        {
            try
            {
                await _authenService.ForgotPasswordAsync(model);
                return Ok("Email sent successfully.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error fogot password: {ex.Message}");
            }
        }
        [HttpPost("verify-reset-code")]
        public async Task<IActionResult> VerifyResetCode([FromBody] VerifyResetCodeRequestModel model)
        {
            try
            {
                // Verify the reset code from the backend
                var isValid = await _authenService.VerifyResetCodeAsync(model.Email, model.Code);
                if (!isValid)
                {
                    return BadRequest("Invalid or expired code.");
                }

                // Respond with a success message
                return Ok("Code verified successfully.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error verifying reset code: {ex.Message}");
            }
        }



        [HttpPost("reset-password")]
        public async Task<IActionResult> ResetPassword([FromBody] ResetPasswordRequestModel model)
        {
            try
            {
                await _authenService.ResetPasswordAsync(model);
                return Ok("Password reset successful.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error verifying reset password: {ex.Message}");
            }
        }

    }
}
