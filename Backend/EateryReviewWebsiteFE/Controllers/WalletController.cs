using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using System.Text;
namespace EateryReviewWebsiteFE.Controllers
{
    public class WalletController : Controller
    {
        public IActionResult Index() => View();

        // Nếu bạn muốn return view sau khi redirect từ PayOS
        public IActionResult Result(string status, string orderCode)
        {
            ViewBag.Status = status;
            ViewBag.OrderCode = orderCode;
            return View(); // Có thể hiện popup thành công/thất bại tại đây
        }
    }
}


