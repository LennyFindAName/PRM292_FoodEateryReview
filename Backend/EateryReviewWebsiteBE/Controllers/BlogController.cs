using BusinessObjects.Models;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.IdentityModel.Tokens;
using System.Reflection.Metadata;
using System.Text;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class BlogController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public BlogController(EateryReviewDbContext context)
        {
            _context = context ?? throw new ArgumentNullException(nameof(context));
        }

        [HttpGet("index")]
        [EnableQuery]
        public IActionResult Index()
        {
            var blogPosts = _context.Blogs
                .Select(blog => new
                {
                    blog.BlogId,
                    blog.BlogTitle,
                    blog.BlogDate,
                    blog.BlogLike,
                    blog.UserId,
                    Username = blog.User.DisplayName,
                    FirstImage = _context.BlogImages
                        .Where(bi => bi.BlogId == blog.BlogId)
                        .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                        .FirstOrDefault()
                })
                .ToList();

            return Ok(blogPosts);
        }

        [HttpGet]
        public IActionResult GetPagedBlogs(int page = 1, int pageSize = 10)
        {
            var skip = (page - 1) * pageSize;
            var blogs = _context.Blogs
                .OrderByDescending(b => b.BlogDate)
                .Skip(skip)
                .Take(pageSize)
                .Select(blog => new
                {
                    blog.BlogId,
                    blog.BlogTitle,
                    blog.BlogDate,
                    blog.BlogLike,
                    blog.UserId,
                    Username = blog.User != null ? blog.User.DisplayName : "Anonymous",
                    FirstImage = _context.BlogImages
                        .Where(bi => bi.BlogId == blog.BlogId)
                        .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                        .FirstOrDefault()
                })
                .ToList();

            var totalCount = _context.Blogs.Count();

            return Ok(new { totalCount, blogs });
        }


        [HttpGet("details/{id}")]
        [EnableQuery]
        public IActionResult Details(int id, [FromQuery] int? userId = null)
        {
            // This method would typically return the details of a specific blog post.
            var blogPost = _context.Blogs.Find(id);
            try
            {
                if (blogPost != null)
                {
                    // Convert blog post to response model
                    var blogResponse = new BlogRequestModel
                    {
                        BlogId = blogPost.BlogId,
                        UserId = blogPost.UserId,
                        BlogTitle = blogPost.BlogTitle,
                        BlogContent = blogPost.BlogContent,
                        BlogDate = blogPost.BlogDate,
                        BlogRate = blogPost.BlogRate,
                        BlogLike = blogPost.BlogLike,
                        BlogStatus = blogPost.BlogStatus,
                        EateryLocationDetail = blogPost.EateryLocationDetail,
                        EateryAddressDetail = blogPost.EateryAddressDetail,
                        FoodQualityRate = blogPost.FoodQualityRate,
                        EnvironmentRate = blogPost.EnvironmentRate,
                        ServiceRate = blogPost.ServiceRate,
                        PricingRate = blogPost.PricingRate,
                        HygieneRate = blogPost.HygieneRate,
                        FoodTypeNames = _context.BlogFoodTypes
                            .Where(bft => bft.BlogId == blogPost.BlogId)
                            .Select(bft => bft.FoodTypeName)
                            .ToList(),
                        MealTypeNames = _context.BlogMealTypes
                            .Where(bmt => bmt.BlogId == blogPost.BlogId)
                            .Select(bmt => bmt.MealTypeName)
                            .ToList(),
                        PriceRanges = _context.BlogPriceRanges
                            .Where(bpr => bpr.BlogId == blogPost.BlogId)
                            .Select(bpr => bpr.PriceRangeValue)
                            .ToList(),
                        BlogBillImageBase64 = Convert.ToBase64String(blogPost.BlogBillImage ?? Array.Empty<byte>()),
                        BlogImagesBase64 = _context.BlogImages
                            .Where(bi => bi.BlogId == blogPost.BlogId)
                            .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                            .ToList(),
                        LikeCount = _context.BlogLikes.Count(bl => bl.BlogId == blogPost.BlogId),
/*                        HasLiked = userId != null && _context.BlogLikes
                         .Any(bl => bl.BlogId == blogPost.BlogId && bl.UserId == userId)*/
                         HasLiked = userId.HasValue && _context.BlogLikes
                          .Any(bl => bl.BlogId == blogPost.BlogId && bl.UserId == userId.Value),


                };

                    return Ok(blogResponse);
                }

                // If blogPost is null, return NotFound
                return NotFound($"Blog post with ID {id} not found.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error retrieving blog post: {ex.Message}");
            }
        }

        [HttpPost("likeblog")]
        public IActionResult LikeBlog([FromForm] int blogID, [FromForm] int userId)
        {
            var existingLike = _context.BlogLikes
                .FirstOrDefault(bl => bl.BlogId == blogID && bl.UserId == userId);

            bool hasLiked;

            if (existingLike != null)
            {
                // Unlike
                _context.BlogLikes.Remove(existingLike);
                hasLiked = false;
            }
            else
            {
                // Like
                var newLike = new BlogLike
                {
                    BlogId = blogID,
                    UserId = userId
                };
                _context.BlogLikes.Add(newLike);
                hasLiked = true;
            }

            _context.SaveChanges();

            var likeCount = _context.BlogLikes.Count(bl => bl.BlogId == blogID);

            return Ok(new { likeCount, hasLiked });
        }

        [HttpPost("create")]
        public IActionResult Create([FromBody] BlogRequestModel blogData)
        {
            // This method would typically create a new blog post.
            if (blogData == null)
            {
                return BadRequest("Blog post cannot be null.");
            }

            try
            {
                // Convert base64 string to byte array for bill image
                byte[]? billImageBytes = null;
                if (!string.IsNullOrEmpty(blogData.BlogBillImageBase64))
                {
                    billImageBytes = Convert.FromBase64String(blogData.BlogBillImageBase64);
                }

                var result = new Blog
                {
                    UserId = blogData.UserId,
                    BlogTitle = blogData.BlogTitle,
                    BlogContent = blogData.BlogContent,
                    BlogDate = blogData.BlogDate ?? DateOnly.FromDateTime(DateTime.Now),
                    BlogBillImage = billImageBytes,
                    BlogRate = blogData.BlogRate,
                    BlogLike = blogData.BlogLike ?? 0,
                    BlogStatus = blogData.BlogStatus ?? 0,
                    EateryLocationDetail = blogData.EateryLocationDetail,
                    EateryAddressDetail = blogData.EateryAddressDetail,
                    FoodQualityRate = blogData.FoodQualityRate,
                    EnvironmentRate = blogData.EnvironmentRate,
                    ServiceRate = blogData.ServiceRate,
                    PricingRate = blogData.PricingRate,
                    HygieneRate = blogData.HygieneRate
                };

                _context.Blogs.Add(result);
                _context.SaveChanges();

                // Add food types
                if (blogData.FoodTypeNames != null && blogData.FoodTypeNames.Any())
                {
                    foreach (var foodType in blogData.FoodTypeNames)
                    {
                        _context.BlogFoodTypes.Add(new BlogFoodType
                        {
                            FoodTypeName = foodType,
                            BlogId = result.BlogId
                        });
                    }
                }

                // Add meal types
                if (blogData.MealTypeNames != null && blogData.MealTypeNames.Any())
                {
                    foreach (var mealType in blogData.MealTypeNames)
                    {
                        _context.BlogMealTypes.Add(new BlogMealType
                        {
                            MealTypeName = mealType,
                            BlogId = result.BlogId
                        });
                    }
                }
              

                // Add price ranges
                if (blogData.PriceRanges != null && blogData.PriceRanges.Any())
                {
                    foreach (var priceRange in blogData.PriceRanges)
                    {
                        _context.BlogPriceRanges.Add(new BlogPriceRange
                        {
                            PriceRangeValue = priceRange,
                            BlogId = result.BlogId
                        });
                    }
                }

                // Add images
                if (blogData.BlogImagesBase64 != null && blogData.BlogImagesBase64.Any())
                {
                    foreach (var imageBase64 in blogData.BlogImagesBase64)
                    {
                        var imageBytes = Convert.FromBase64String(imageBase64);
                        _context.BlogImages.Add(new BlogImage
                        {
                            BlogId = result.BlogId,
                            BlogImage1 = imageBytes
                        });
                    }
                    _context.SaveChanges();
                }

                return Ok(blogData);
                //return CreatedAtAction(nameof(Details), new { id = result.BlogId }, blogData);
            }
            catch (Exception ex)
            {
                return BadRequest($"Error creating blog: {ex.Message}");
            }
        }


        //For App
        [HttpGet("list")] // api/blog/list
        public IActionResult GetPagedBlogsApp(
            [FromQuery] int page = 1,
            [FromQuery] int pageSize = 10)
        {
            var skip = (page - 1) * pageSize;
            var blogs = _context.Blogs
                .OrderByDescending(b => b.BlogDate)
                .Skip(skip)
                .Take(pageSize)
                .Where(p => p.BlogStatus == 1) // Assuming 1 is the status for published blogs
                .Select(blog => new
                {
                    blog.BlogId,
                    blog.BlogTitle,
                    blog.BlogDate,
                    blog.BlogLike,
                    blog.UserId,
                    Username = blog.User != null ? blog.User.Username : "Anonymous",
                     FirstImage = _context.BlogImages
                         .Where(bi => bi.BlogId == blog.BlogId)
                         .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                         .FirstOrDefault()
                })
            .ToList();

            var totalCount = _context.Blogs.Count();
            Response.Headers.Add("X-Total-Count", totalCount.ToString());

            return Ok(blogs);
        }


        [HttpGet("detailsApp/{id}")]
        [EnableQuery]
        public IActionResult DetailsApp(int id, [FromQuery] int? userId = null)
        {
            // This method would typically return the details of a specific blog post.
            var blogPost = _context.Blogs.Find(id);
            try
            {
                if (blogPost != null)
                {
                    // Convert blog post to response model
                    var blogResponse = new BlogRequestModel
                    {
                        BlogId = blogPost.BlogId,
                        UserId = blogPost.UserId,
                        BlogTitle = blogPost.BlogTitle,
                        BlogContent = blogPost.BlogContent,
                        BlogDate = blogPost.BlogDate,
                        BlogRate = blogPost.BlogRate,
                        BlogLike = blogPost.BlogLike,
                        BlogStatus = blogPost.BlogStatus,
                        EateryLocationDetail = blogPost.EateryLocationDetail,
                        EateryAddressDetail = blogPost.EateryAddressDetail,
                        FoodQualityRate = blogPost.FoodQualityRate,
                        EnvironmentRate = blogPost.EnvironmentRate,
                        ServiceRate = blogPost.ServiceRate,
                        PricingRate = blogPost.PricingRate,
                        HygieneRate = blogPost.HygieneRate,
                        FoodTypeNames = _context.BlogFoodTypes
                            .Where(bft => bft.BlogId == blogPost.BlogId)
                            .Select(bft => bft.FoodTypeName)
                            .ToList(),
                        MealTypeNames = _context.BlogMealTypes
                            .Where(bmt => bmt.BlogId == blogPost.BlogId)
                            .Select(bmt => bmt.MealTypeName)
                            .ToList(),
                        PriceRanges = _context.BlogPriceRanges
                            .Where(bpr => bpr.BlogId == blogPost.BlogId)
                            .Select(bpr => bpr.PriceRangeValue)
                            .ToList(),
                        BlogImagesBase64 = _context.BlogImages
                            .Where(bi => bi.BlogId == blogPost.BlogId)
                            .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                            .ToList(),
                        LikeCount = _context.BlogLikes.Count(bl => bl.BlogId == blogPost.BlogId),
                        /*                        HasLiked = userId != null && _context.BlogLikes
                                                 .Any(bl => bl.BlogId == blogPost.BlogId && bl.UserId == userId)*/
                        HasLiked = userId.HasValue && _context.BlogLikes
                          .Any(bl => bl.BlogId == blogPost.BlogId && bl.UserId == userId.Value),
                    };

                    return Ok(blogResponse);
                }

                // If blogPost is null, return NotFound
                return NotFound($"Blog post with ID {id} not found.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error retrieving blog post: {ex.Message}");
            }
        }
    }
}
