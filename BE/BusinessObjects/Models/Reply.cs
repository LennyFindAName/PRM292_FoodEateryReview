using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Reply
{
    public int ReplyId { get; set; }

    public string? Content { get; set; }

    public int? ReplyStatus { get; set; }

    public int? CommentId { get; set; }

    public int? ReplyLike { get; set; }

    public DateTime ReplyDate { get; set; }

    public int? UserId { get; set; }

    public virtual Comment? Comment { get; set; }

    public virtual ICollection<ReplyLike> ReplyLikes { get; set; } = new List<ReplyLike>();

    public virtual User? User { get; set; }
}
