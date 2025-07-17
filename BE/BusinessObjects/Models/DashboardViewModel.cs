using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.Models
{
    public class DashboardViewModel
    {
        public int TotalBlogs { get; set; }
        public int NewBlogsToday { get; set; }
        public int TotalUsers { get; set; }
        public int ActiveUsersToday { get; set; }

        public List<Blog> Blogs { get; set; }
        public int TotalCount { get; set; }
    }

}
