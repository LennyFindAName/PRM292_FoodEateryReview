using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.RequestModels
{
    public class AddModeratorModel
    {
        [Required]
        public string Username { get; set; }

        [Required]
        public string DisplayName { get; set; }

        [Required, EmailAddress]
        public string UserEmail { get; set; }

        [Required]
        public string Password { get; set; }
    }
}
