using Microsoft.AspNetCore.Mvc;

namespace EateryReviewWebsiteFE.Controllers
{
    public class BlogController : Controller
    {
        private readonly IConfiguration _configuration;
        public BlogController(IConfiguration configuration)
        {
            _configuration = configuration;
        }
        public IActionResult Index()
        {
            return View();
        }
        public IActionResult Details()
        {
            ViewBag.GoogleMapsApiKey = _configuration["GoogleMapAPI:Key"];
            return View();
        }

        public IActionResult Create()
        {
            ViewBag.GoogleMapsApiKey = _configuration["GoogleMapAPI:Key"];
            return View();
        }
    }
}
