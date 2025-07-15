using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace Services.Helper
{
    public class EmailHelper
    {
        private readonly IConfiguration _config;

        public EmailHelper(IConfiguration config)
        {
            _config = config;
        }

        public async Task sendEmailAsync(string to, string subject, string message)
        {
            var from = _config["Email:From"];
            var smtp = _config["Email:Smtp"];
            var port = int.Parse(_config["Email:Port"]);
            var password = _config["Email:Password"];

            using var client = new SmtpClient(smtp, port)
            {
                Credentials = new NetworkCredential(from, password),
                EnableSsl = true
            };

            var mail = new MailMessage(from, to,subject, message);
            await client.SendMailAsync(mail);
            
        }
    }
}
