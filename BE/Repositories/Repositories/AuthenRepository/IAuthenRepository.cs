using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Repositories.Repositories.AuthenRepository
{
    public interface IAuthenRepository
    {
        Task<User> GetByUsernameAsync(string username);
        Task AddAsync(User user);

        Task<User> GetByEmailAsync(string email);
        Task UpdateAsync(User user);

        Task<bool> CheckEmailExistenceAsync(string email);
    }
}

