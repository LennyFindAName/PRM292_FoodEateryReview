using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class BlogImage
{
    public int ImageId { get; set; }

    public int? BlogId { get; set; }

    public byte[]? BlogImage1 { get; set; }

    public virtual Blog? Blog { get; set; }
}
