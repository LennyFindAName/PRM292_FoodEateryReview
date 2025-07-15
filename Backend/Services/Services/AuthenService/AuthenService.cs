using AutoMapper;
using BusinessObjects.Models;
using BusinessObjects.RequestModels.Authen;
using BusinessObjects.ResponseModels.Authen;
using Microsoft.Extensions.Configuration;
using Repositories.Repositories.AuthenRepository;
using Services.Helper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Services.AuthenService
{
    public class AuthenService : IAuthenService
    {
        private readonly IAuthenRepository _authenRepository;
        private readonly IMapper _mapper;
        private readonly IConfiguration _config;
        private readonly EmailHelper _emailHelper;

        public AuthenService(IAuthenRepository authenRepository, IMapper mapper, IConfiguration config, EmailHelper emailHelper)
        {
            _authenRepository = authenRepository;
            _mapper = mapper;
            _config = config;
            _emailHelper = emailHelper;
        }
        public async Task<LoginResponseModel> LoginAsync(LoginRequestModel model)
        {
            var user = await _authenRepository.GetByUsernameAsync(model.Username);
            if (user == null || !BCrypt.Net.BCrypt.Verify(model.Password, user.Password))
                throw new UnauthorizedAccessException("Invalid username or password");

            user.LastLogin = DateTime.Now;
            await _authenRepository.UpdateAsync(user);

            var response = _mapper.Map<LoginResponseModel>(user);
            response.AccessToken = JwtHelper.GenerateJwtToken(user.UserId.ToString(),user.Username, user.RoleId.ToString(), _config);
            if (string.IsNullOrEmpty(response.AccessToken))
            {
                throw new UnauthorizedAccessException("Failed to generate access token");
            }
            return response;
        }

        public async Task RegisterAsync(RegisterRequestModel model)
        {
            //check username exist
            var existingUser = await _authenRepository.GetByUsernameAsync(model.Username);
            if (existingUser != null)
                throw new Exception("Username already exists");

            //check email exist
            var existingEmail = await _authenRepository.GetByEmailAsync(model.Email);
            if (existingEmail != null)
                throw new Exception("Email already exists");

            var user = _mapper.Map<User>(model);
            user.Password = BCrypt.Net.BCrypt.HashPassword(model.Password);
            user.RoleId = 2;
            user.UserStatus = 1;
            user.DisplayName = model.Username;
            user.UserPhone = model?.Phone;
            await _authenRepository.AddAsync(user);
        }
        public async Task ForgotPasswordAsync(ForgotPasswordRequestModel model)
        {
            var user = await _authenRepository.GetByEmailAsync(model.Email);
            if (user == null)
            {
                throw new Exception("Email not found in the system");
            }

            var resetCode = Guid.NewGuid().ToString("N").Substring(0, 6).ToUpper().Trim();

            //Save resetCode + time
            user.PasswordResetCode = resetCode;
            user.PasswordResetExpiration = DateTime.Now.AddMinutes(10);
            await _authenRepository.UpdateAsync(user);


            // Tạo URL reset mật khẩu
            var resetLink = $"http://localhost:5143/authen/VerifyResetCode?email={user.UserEmail}&code={resetCode}";

            try
            {
                //create email 
                var subject = "Mã xác nhận đặt lại mật khẩu";
                var message = $"Xin chào {user.Username},\n\n" +
                      $"Để đặt lại mật khẩu, vui lòng nhấp vào đường link sau: \n" +
                      $"{resetLink}\n\nMã reset có hiệu lực trong 10 phút.\n\nTrân trọng,\nEateryReview Team";

                //send email
                await _emailHelper.sendEmailAsync(user.UserEmail, subject, message);
            }
            catch (Exception ex)
            {
                throw new Exception($"Error send email: {ex.Message}");
            }

        }

        public async Task ResetPasswordAsync(ResetPasswordRequestModel model)
        {
            if (model.NewPassword != model.ConfirmPassword)
                throw new Exception("New password and confirm password do not match");

            var user = await _authenRepository.GetByEmailAsync(model.Email);
            if (user == null || user.PasswordResetCode != model.Code || user.PasswordResetExpiration < DateTime.UtcNow)
                throw new Exception("Invalid or expired reset code.");

            user.Password = BCrypt.Net.BCrypt.HashPassword(model.NewPassword);
            user.PasswordResetCode = null;// Clear the reset code after successful reset
            user.PasswordResetExpiration = null;// Clear the expiration time

            await _authenRepository.UpdateAsync(user);
        }

        public async Task<bool> VerifyResetCodeAsync(string email, string code)
        {
            var user = await _authenRepository.GetByEmailAsync(email);
            if (user == null || user.PasswordResetCode != code)
                return false;

            if (user.PasswordResetExpiration < DateTime.Now)
                return false;

            return true;
        }

        public Task<bool> CheckEmailExistenceAsync(string email)
        {
            throw new NotImplementedException();
        }
    }
}
