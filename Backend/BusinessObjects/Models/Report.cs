using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Report
{
    public int ReportId { get; set; }

    public int? BlogId { get; set; }

    public int? ReportReasonId { get; set; }

    public int? ReportStatus { get; set; }

    public DateTime? ReportTime { get; set; }

    public int? ReporterId { get; set; }

    public virtual Blog? Blog { get; set; }

    public virtual ReportReason? ReportReason { get; set; }

    public virtual User? Reporter { get; set; }
}
