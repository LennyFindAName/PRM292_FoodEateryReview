using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class User
{
    public int UserId { get; set; }

    public string? DisplayName { get; set; }

    public string? Username { get; set; }

    public string? Password { get; set; }

    public string? UserEmail { get; set; }

    public byte[]? UserImage { get; set; }

    public string? UserPhone { get; set; }

    public int? RoleId { get; set; }

    public int? UserStatus { get; set; }

    public string? PasswordResetCode { get; set; }

    public DateTime? PasswordResetExpiration { get; set; }

    public int WalletBalance { get; set; }

    public DateTime? LastLogin { get; set; }

    public string? ModeratorNote { get; set; }

    public virtual ICollection<BlogLike> BlogLikes { get; set; } = new List<BlogLike>();

    public virtual ICollection<Blog> Blogs { get; set; } = new List<Blog>();

    public virtual ICollection<Bookmark> Bookmarks { get; set; } = new List<Bookmark>();

    public virtual ICollection<CommentLike> CommentLikes { get; set; } = new List<CommentLike>();

    public virtual ICollection<Comment> Comments { get; set; } = new List<Comment>();

    public virtual ICollection<Reply> Replies { get; set; } = new List<Reply>();

    public virtual ICollection<ReplyLike> ReplyLikes { get; set; } = new List<ReplyLike>();

    public virtual ICollection<Report> Reports { get; set; } = new List<Report>();

    public virtual ICollection<Ticket> Tickets { get; set; } = new List<Ticket>();

    public virtual ICollection<WalletTransaction> WalletTransactions { get; set; } = new List<WalletTransaction>();
}
