using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class BlogMealType
{
    public int? BlogId { get; set; }

    public string? MealTypeName { get; set; }

    public int Id { get; set; }

    public virtual Blog? Blog { get; set; }
}
