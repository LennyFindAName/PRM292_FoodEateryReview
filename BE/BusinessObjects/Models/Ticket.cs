using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Ticket
{
    public int TicketId { get; set; }

    public int? TypeId { get; set; }

    public string? TicketContent { get; set; }

    public DateTime? TicketTime { get; set; }

    public int? TicketStatus { get; set; }

    public int? CreatorId { get; set; }

    public virtual User? Creator { get; set; }

    public virtual TicketType? Type { get; set; }
}
