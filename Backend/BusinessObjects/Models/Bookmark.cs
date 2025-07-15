using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Bookmark
{
    public int BookmarkId { get; set; }

    public int? BookmarkByUserId { get; set; }

    public int? BlogId { get; set; }

    public virtual Blog? Blog { get; set; }

    public virtual User? BookmarkByUser { get; set; }
}
