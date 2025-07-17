using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ModerationModels.User
{
    public class UserViewModel
    {
        public int UserId { get; set; }
        public string DisplayName { get; set; }
        public string Username { get; set; }
        public string UserEmail { get; set; }
        public int UserStatus { get; set; }
    }
}
