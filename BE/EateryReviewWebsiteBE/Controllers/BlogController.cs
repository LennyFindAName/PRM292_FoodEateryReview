using BusinessObjects.Enums;
using BusinessObjects.Models;
using BusinessObjects.ModerationModels.Blog;
using BusinessObjects.RequestModels;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OData.Query;
using Microsoft.IdentityModel.Tokens;
using Services.Services.NotificationService;
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

        /*[HttpGet("index")]
        [EnableQuery]
        public IActionResult Index()
        {
            var blogPosts = _context.Blogs
                .Where(blog => blog.BlogStatus == (int)BlogModerationStatus.Approved)
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
        }*/

        [HttpGet]
        public IActionResult GetPagedBlogs(int page = 1, int pageSize = 10)
        {
            var skip = (page - 1) * pageSize;
            var blogs = _context.Blogs
                .OrderByDescending(b => b.BlogDate)
                .Where(b => b.BlogStatus == (int)BlogModerationStatus.Approved)
                .Skip(skip)
                .Take(pageSize)
                .Select(blog => new
                {
                    blog.BlogId,
                    blog.BlogTitle,
                    blog.BlogDate,
                    blog.BlogLike,
                    blog.UserId,
                    Username = blog.User != null ? blog.User.DisplayName : "N/A",
                    FirstImage = _context.BlogImages
                        .Where(bi => bi.BlogId == blog.BlogId)
                        .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                          .FirstOrDefault(),
                    ProfileImage = blog.User != null && blog.User.UserImage != null ? Convert.ToBase64String(blog.User.UserImage) : null
                })
                .ToList();

            var totalCount = _context.Blogs.Count();
            //Response.Headers.Add("X-Total-Count", totalCount.ToString());

            return Ok(new { totalCount, blogs });
        }

        [HttpGet("paidBlogs")]
        public IActionResult GetPaidBlogList()
        {
            var currentDate = DateTime.Now;

            // First try to get currently paid blogs (with valid expiration dates)
            var paidBlogs = _context.Blogs
                .Where(b => b.BlogStatus == (int)BlogModerationStatus.Approved
                           && b.PaidExpirationDate.HasValue
                           && b.PaidExpirationDate.Value > currentDate)
                .Select(blog => new
                {
                    blog.BlogId,
                    blog.BlogTitle,
                    blog.BlogDate,
                    blog.BlogLike,
                    blog.UserId,
                    Username = blog.User != null ? blog.User.DisplayName : "N/A",
                    FirstImage = _context.BlogImages
                        .Where(bi => bi.BlogId == blog.BlogId)
                        .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                        .FirstOrDefault(),
                    ProfileImage = blog.User != null && blog.User.UserImage != null ? Convert.ToBase64String(blog.User.UserImage) : null
                })
                .OrderBy(x => Guid.NewGuid()) // Randomize the order
                .Take(4)
                .ToList();

            // If we don't have enough paid blogs, fill with regular approved blogs
            if (paidBlogs.Count < 4)
            {
                var neededCount = 4 - paidBlogs.Count;
                var paidBlogIds = paidBlogs.Select(b => b.BlogId).ToList();

                var regularBlogs = _context.Blogs
                    .Where(b => b.BlogStatus == (int)BlogModerationStatus.Approved
                               && !paidBlogIds.Contains(b.BlogId)) // Exclude already selected paid blogs
                    .Select(blog => new
                    {
                        blog.BlogId,
                        blog.BlogTitle,
                        blog.BlogDate,
                        blog.BlogLike,
                        blog.UserId,
                        Username = blog.User != null ? blog.User.DisplayName : "N/A",
                        FirstImage = _context.BlogImages
                            .Where(bi => bi.BlogId == blog.BlogId)
                            .Select(bi => Convert.ToBase64String(bi.BlogImage1 ?? Array.Empty<byte>()))
                            .FirstOrDefault(),
                        ProfileImage = blog.User != null && blog.User.UserImage != null ? Convert.ToBase64String(blog.User.UserImage) : null
                    })
                    .OrderBy(x => Guid.NewGuid())
                    .Take(neededCount)
                    .ToList();

                // Combine paid and regular blogs
                var allBlogs = paidBlogs.Cast<object>().Concat(regularBlogs.Cast<object>()).ToList();
                return Ok(allBlogs.OrderBy(x => Guid.NewGuid()).ToList());
            }

            return Ok(paidBlogs);
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
                          .Any(bl => bl.BlogId == blogPost.BlogId && bl.UserId == userId.Value)

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
        public async Task<IActionResult> CreateAsync([FromBody] BlogRequestModel blogData)
        {
            if (blogData == null)
            {
                return BadRequest("Bài viết không được để trống.");
            }

            //Check if current user has enough credits to create a blog
            var user = _context.Users.Find(blogData.UserId);
            if (user == null)
            {
                return NotFound(new { error = $"Người dùng ID : {blogData.UserId} không tìm thấy." });
            }

            if(blogData.DisplayPaidCost != BlogPaidOptions.None.GetPrice()) 
            { 
                if(blogData.DisplayPaidCost > user.WalletBalance)
                {
                    return BadRequest(new { error = "Tài khoản của bạn không đủ tiền để thực hiện chức năng này" });
                }

            }


            // If the user has paid for a blog, set the expiration date
            DateTime currentDate = DateTime.Now;
            DateTime? paidExpDay = null;

            foreach(var option in Enum.GetValues(typeof(BlogPaidOptions)))
            {
                if (option is BlogPaidOptions.None)
                {
                    continue;
                }
                var blogPaidOption = (BlogPaidOptions)option;
                if (blogData.DisplayPaidCost == blogPaidOption.GetPrice())
                {
                    paidExpDay = currentDate.AddDays(blogPaidOption.GetDay());
                    //user.WalletBalance -= blogPaidOption.GetPrice();
                    break;
                }
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
                    BlogStatus = blogData.BlogStatus ?? (int) BlogModerationStatus.Pending,
                    EateryLocationDetail = blogData.EateryLocationDetail,
                    EateryAddressDetail = blogData.EateryAddressDetail,
                    FoodQualityRate = blogData.FoodQualityRate,
                    EnvironmentRate = blogData.EnvironmentRate,
                    ServiceRate = blogData.ServiceRate,
                    PricingRate = blogData.PricingRate,
                    HygieneRate = blogData.HygieneRate,
                    PaidExpirationDate = paidExpDay
                };

                _context.Blogs.Add(result);
                await _context.SaveChangesAsync();

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
                    await _context.SaveChangesAsync();
                }

                try
                {
                    using var httpClient = new HttpClient();
                    var aiEndpoint = $"{Request.Scheme}://{Request.Host}/api/moderation/assess-by-ai/{result.BlogId}";
                    var aiResponse = await httpClient.PostAsync(aiEndpoint, null);

                    if (aiResponse.IsSuccessStatusCode)
                    {
                        var responseContent = await aiResponse.Content.ReadAsStringAsync();
                        using var document = System.Text.Json.JsonDocument.Parse(responseContent);

                        // Check if the AI assessment returned status 1 (Approved)
                        if (document.RootElement.TryGetProperty("status", out var statusProperty))
                        {
                            var status = statusProperty.GetInt32();
                            if (status == 1 && blogData.DisplayPaidCost > 0)
                            {
                                user.WalletBalance -= (int)blogData.DisplayPaidCost;
                                await _context.SaveChangesAsync();
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    result.Opinion = $"Error assessing by AI: {ex.Message}";
                    result.BlogStatus = (int)BlogModerationStatus.Pending;
                    await _context.SaveChangesAsync();
                }

                await NotificationService.SendAsync(_context, blogData.UserId.Value, $"Bài viết '{blogData.BlogTitle}' đã được tạo thành công.", MessageType.Success);
                return Ok(blogData);
                //return CreatedAtAction(nameof(Details), new { id = result.BlogId }, blogData);
            }
            catch (Exception ex)
            {
                await NotificationService.SendAsync(_context, blogData.UserId.Value, $"Lỗi khi tạo bài viết: {ex.Message}", MessageType.Failure);
                return BadRequest($"Lỗi khi tạo bài: {ex.Message}");
            }
        }

        [HttpGet("searchBlogs")]
        [EnableQuery]
        public IActionResult GetSearchedPagedBlogs(string result = "", int page = 1, int pageSize = 10, string mealType = "", string foodType = "", string priceRange = "")
        {
            var skip = (page - 1) * pageSize;

            // Start with all blogs and apply filters
            var query = _context.Blogs.AsQueryable();

            // Apply text search filter for blog title if result parameter is provided
            if (!string.IsNullOrEmpty(result))
            {
                query = query.Where(b => b.BlogTitle.Contains(result));
            }

            // Apply filters only if parameters are provided
            if (!string.IsNullOrEmpty(mealType))
            {
                query = query.Where(b => b.BlogMealTypes.Any(bmt => bmt.MealTypeName.Contains(mealType)));
            }

            if (!string.IsNullOrEmpty(foodType))
            {
                query = query.Where(b => b.BlogFoodTypes.Any(bft => bft.FoodTypeName.Contains(foodType)));
            }

            if (!string.IsNullOrEmpty(priceRange))
            {
                query = query.Where(b => b.BlogPriceRanges.Any(bpr => bpr.PriceRangeValue.Contains(priceRange)));
            }

            // Get total count of filtered results
            var totalCount = query.Count();

            // Apply ordering, pagination, and projection
            var blogs = query
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

            return Ok(new { totalCount, blogs });
        }
    }
}
