using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class BlogPriceRange
{
    public int? BlogId { get; set; }

    public string? PriceRangeValue { get; set; }

    public int Id { get; set; }

    public virtual Blog? Blog { get; set; }
}
