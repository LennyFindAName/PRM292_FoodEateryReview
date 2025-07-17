using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.RequestModels
{
    public class ReportRequestModel
    {
        public int BlogId { get; set; }
        public int ReportReasonId { get; set; }
        public int ReporterId { get; set; }
        public string? ReportContent { get; set; }

    }
}
