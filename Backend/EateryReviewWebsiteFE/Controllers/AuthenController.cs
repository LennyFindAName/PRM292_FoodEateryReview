using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class AuthenController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }
        public IActionResult ForgotPassword()
        {
            return View();
        }
        public IActionResult ResetPassword()
        {
            return View();
        }
        public IActionResult VerifyResetCode()
        {
            return View();
        }

    }
}
