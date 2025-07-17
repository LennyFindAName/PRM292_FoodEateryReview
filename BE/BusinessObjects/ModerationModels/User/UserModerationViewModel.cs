using System.Collections.Generic;

namespace BusinessObjects.ModerationModels.User
{
    public class UserModerationViewModel
    {
        public List<UserModerationDto> Users { get; set; }
        public int CurrentPage { get; set; }
        public int TotalPages { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public int? Status { get; set; }
    }
}