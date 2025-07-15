using BusinessObjects.RequestModels.Authen;
using BusinessObjects.ResponseModels.Authen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Services.AuthenService
{
    public interface IAuthenService
    {
        Task<LoginResponseModel> LoginAsync(LoginRequestModel model);
        Task RegisterAsync(RegisterRequestModel model);

        Task ForgotPasswordAsync(ForgotPasswordRequestModel model);
        Task<bool> VerifyResetCodeAsync(string email, string code);
        Task ResetPasswordAsync(ResetPasswordRequestModel model);
    }
}
