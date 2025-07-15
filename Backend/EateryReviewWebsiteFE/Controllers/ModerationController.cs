using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class ModerationController : Controller
    {
        public IActionResult Review()
        {
            return View();
        }
    }
}
