using BusinessObjects.Models;

namespace BusinessObjects.ModerationModels.Blog
{
    public class BlogModerationViewModel
    {
        public List<BlogModerationDto> Blogs { get; set; } = new();
        public int CurrentPage { get; set; }
        public int TotalPages { get; set; }
        public string Title { get; set; }
        public string Username { get; set; }
        public string DateFrom { get; set; }
        public string DateTo { get; set; }
        public int? Status { get; set; }
    }
}