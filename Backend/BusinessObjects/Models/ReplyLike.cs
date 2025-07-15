using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class ReplyLike
{
    public int LikeId { get; set; }

    public int? ReplyId { get; set; }

    public int? UserId { get; set; }

    public virtual Reply? Reply { get; set; }

    public virtual User? User { get; set; }
}
