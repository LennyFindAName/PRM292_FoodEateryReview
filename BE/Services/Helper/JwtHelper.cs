using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace Services.Helper
{
    public static class JwtHelper
    {
        // Cập nhật hàm để thêm tham số userName
        public static string GenerateJwtToken(string userId, string userName, string role, IConfiguration config)
        {
            try
            {
                var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(config["Jwt:Secret"]!));
                var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

                // Thêm claim "name" vào token
                var claims = new[]
{
    new Claim(ClaimTypes.NameIdentifier, userId), // <-- bắt buộc để Get userId
    new Claim(ClaimTypes.Name, userName),
    new Claim(ClaimTypes.Role, role)
};


                var token = new JwtSecurityToken(
                    issuer: config["Jwt:Issuer"],
                    audience: config["Jwt:Audience"],
                    claims: claims,
                    expires: DateTime.UtcNow.AddMinutes(60),
                    signingCredentials: creds
                );

                return new JwtSecurityTokenHandler().WriteToken(token);
            }
            catch (Exception ex)
            {
                // Log the exception if any error occurs during JWT generation
                Console.Error.WriteLine($"Error generating JWT token: {ex.Message}");
                return null;
            }
        }
    }
}
