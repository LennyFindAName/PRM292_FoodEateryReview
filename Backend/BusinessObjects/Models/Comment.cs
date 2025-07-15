using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Comment
{
    public int CommentId { get; set; }

    public string? Content { get; set; }

    public int? CommentStatus { get; set; }

    public int? UserId { get; set; }

    public int? BlogId { get; set; }

    public int? CommentLike { get; set; }

    public DateTime CommentDate { get; set; }

    public virtual Blog? Blog { get; set; }

    public virtual ICollection<CommentLike> CommentLikes { get; set; } = new List<CommentLike>();

    public virtual ICollection<Reply> Replies { get; set; } = new List<Reply>();

    public virtual User? User { get; set; }
}
