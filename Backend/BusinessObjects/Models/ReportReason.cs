using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class ReportReason
{
    public int ReasonId { get; set; }

    public string? BlogReasonContent { get; set; }

    public virtual ICollection<Report> Reports { get; set; } = new List<Report>();
}
