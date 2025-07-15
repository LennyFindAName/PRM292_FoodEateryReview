using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ResponseModels
{
    public class AccountResponseModel
    {
        public int UserId { get; set; }

        public string? DisplayName { get; set; }

        public string? Username { get; set; }

        public string? Password { get; set; }

        public string? UserEmail { get; set; }

        public byte[]? UserImage { get; set; }

        public string? UserPhone { get; set; }

        public int? RoleId { get; set; }

        public int? UserStatus { get; set; }

    }
}
