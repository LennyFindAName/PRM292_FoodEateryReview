using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class BlogLike
{
    public int LikeId { get; set; }

    public int? BlogId { get; set; }

    public int? UserId { get; set; }

    public virtual Blog? Blog { get; set; }

    public virtual User? User { get; set; }
}
