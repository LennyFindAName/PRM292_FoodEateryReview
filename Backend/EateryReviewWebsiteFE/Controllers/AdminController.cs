using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using BusinessObjects.Models;
using BusinessObjects.RequestModels;

public class AdminController : Controller
{
    private readonly IHttpClientFactory _clientFactory;

    public AdminController(IHttpClientFactory clientFactory)
    {
        _clientFactory = clientFactory;
    }

    public async Task<IActionResult> Dashboard()
    {
        var client = _clientFactory.CreateClient("ApiClient");
        var dashboard = await client.GetFromJsonAsync<DashboardViewModel>("api/Admin/dashboard");
        return View(dashboard);
    }

    public async Task<IActionResult> ManageModerators()
    {
        var client = _clientFactory.CreateClient("ApiClient");
        var mods = await client.GetFromJsonAsync<List<User>>("api/Admin/moderators");
        return View(mods);
    }

    [HttpPost]
    public async Task<IActionResult> ToggleUserStatus(int id)
    {
        var client = _clientFactory.CreateClient("ApiClient");
        var response = await client.PostAsJsonAsync($"api/Admin/toggle-user-status/{id}", new { });
        
        if (response.IsSuccessStatusCode)
        {
            return RedirectToAction("ManageModerators");
        }
        
        ModelState.AddModelError(string.Empty, "Failed to toggle status.");
        return View();
    }
}
