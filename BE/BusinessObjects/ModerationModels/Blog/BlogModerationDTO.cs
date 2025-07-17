using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ModerationModels.Blog
{
    public class BlogModerationDto
    {
        public int BlogId { get; set; }
        public string BlogTitle { get; set; }
        public DateOnly? BlogDate { get; set; }
        public int? BlogLike { get; set; }
        public int? BlogStatus { get; set; }
        public string Opinion { get; set; }
        public int? UserId { get; set; }
        public string UserDisplayName { get; set; }
        public List<string> Images { get; set; } = new();
    }
}
