using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.RequestModels
{
    public class ReplyRequestModel
    {
        public string Content { get; set; } 
        public int CommentId { get; set; }
        public int UserId { get; set; }
    }
}
