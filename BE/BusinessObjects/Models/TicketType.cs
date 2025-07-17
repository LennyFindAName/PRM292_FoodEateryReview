using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class TicketType
{
    public int TypeId { get; set; }

    public string? TicketTypeContent { get; set; }

    public virtual ICollection<Ticket> Tickets { get; set; } = new List<Ticket>();
}
