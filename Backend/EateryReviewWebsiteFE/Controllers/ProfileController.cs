using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class ProfileController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Edit()
        {
            return View();
        }
    }
}
