using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ResponseModels
{
    public class BlogResponseModel
    {
        // Existing properties
        public int BlogId { get; set; }
        public int? UserId { get; set; }
        public string? BlogTitle { get; set; }
        public string? BlogContent { get; set; }
        public DateOnly? BlogDate { get; set; }

        public double? BlogRate { get; set; }
        public int? BlogLike { get; set; }
        public int? BlogStatus { get; set; }
        public string? EateryLocationDetail { get; set; }
        public string? EateryAddressDetail { get; set; } //Used to display GG Maps
        public int? FoodQualityRate { get; set; }
        public int? EnvironmentRate { get; set; }
        public int? ServiceRate { get; set; }
        public int? PricingRate { get; set; }
        public int? HygieneRate { get; set; }
        public List<string> FoodTypeNames { get; set; } = new List<string>();
        public List<string> MealTypeNames { get; set; } = new List<string>();
        public List<string> PriceRanges { get; set; } = new List<string>();


        // New properties for base64 image data

        public string? BlogBillImageBase64 { get; set; }
        public List<string> BlogImagesBase64 { get; set; } = new List<string>();


        // Properties for byte arrays (not important yet)
        public byte[]? BlogBillImage { get; set; }
        public List<byte[]> BlogImages { get; set; } = new List<byte[]>();
    }
}
