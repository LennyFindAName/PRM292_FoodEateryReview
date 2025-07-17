using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class Blog
{
    public int BlogId { get; set; }

    public int? UserId { get; set; }

    public string? BlogTitle { get; set; }

    public string? BlogContent { get; set; }

    public DateOnly? BlogDate { get; set; }

    public byte[]? BlogBillImage { get; set; }

    public double? BlogRate { get; set; }

    public int? BlogLike { get; set; }

    public int? EateryId { get; set; }

    public int? BlogStatus { get; set; }

    public string? EateryLocationDetail { get; set; }

    public string? EateryNameDetail { get; set; }

    public string? EateryAddressDetail { get; set; }

    public int? FoodQualityRate { get; set; }

    public int? EnvironmentRate { get; set; }

    public int? ServiceRate { get; set; }

    public int? PricingRate { get; set; }

    public int? HygieneRate { get; set; }

    public string? Opinion { get; set; }

    public DateTime? PaidExpirationDate { get; set; }

    public virtual ICollection<BlogFoodType> BlogFoodTypes { get; set; } = new List<BlogFoodType>();

    public virtual ICollection<BlogImage> BlogImages { get; set; } = new List<BlogImage>();

    public virtual ICollection<BlogLike> BlogLikes { get; set; } = new List<BlogLike>();

    public virtual ICollection<BlogMealType> BlogMealTypes { get; set; } = new List<BlogMealType>();

    public virtual ICollection<BlogPriceRange> BlogPriceRanges { get; set; } = new List<BlogPriceRange>();

    public virtual ICollection<Bookmark> Bookmarks { get; set; } = new List<Bookmark>();

    public virtual ICollection<Comment> Comments { get; set; } = new List<Comment>();

    public virtual ICollection<Report> Reports { get; set; } = new List<Report>();

    public virtual User? User { get; set; }
}
