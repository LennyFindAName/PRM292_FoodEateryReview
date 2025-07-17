using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class BlogFoodType
{
    public int? BlogId { get; set; }

    public string? FoodTypeName { get; set; }

    public int Id { get; set; }

    public virtual Blog? Blog { get; set; }
}
