using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repositories.Repositories.AuthenRepository
{
    public class AuthenRepository : IAuthenRepository
    {
        private readonly EateryReviewDbContext _context;

        public AuthenRepository(EateryReviewDbContext context)
        {
            _context = context;
        }

        public async Task AddAsync(User user)
        {
            await _context.Users.AddAsync(user);
            await _context.SaveChangesAsync();
        }

        public async Task<bool> CheckEmailExistenceAsync(string email)
        {
            return await _context.Users.AnyAsync(u => u.UserEmail == email); // Assuming you have a Users table
        }

        public async Task<User> GetByEmailAsync(string email)
        {
            return await _context.Users.FirstOrDefaultAsync(u => u.UserEmail == email);
        }

        public async Task<User> GetByUsernameAsync(string username)
        {
            return await _context.Users.FirstOrDefaultAsync(u => u.Username == username);
        }

        public async Task UpdateAsync(User user)
        {
           _context.Users.Update(user);
            await _context.SaveChangesAsync();
        }
    }
}
