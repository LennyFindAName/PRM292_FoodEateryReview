package prm392.project.model.DTOs;

import java.util.List;

public class BlogRequest {
    private Integer blogId;
    private Integer userId;
    private String blogTitle;
    private String blogContent;
    private String blogDate; // Use String for date, or Date if you prefer
    private Double blogRate;
    private Integer blogLike;
    private Integer blogStatus;
    private String eateryLocationDetail;
    private String eateryAddressDetail;
    private Integer foodQualityRate;
    private Integer environmentRate;
    private Integer serviceRate;
    private Integer pricingRate;
    private Integer hygieneRate;
    private List<String> foodTypeNames;
    private List<String> mealTypeNames;
    private List<String> priceRanges;
    private String blogBillImageBase64;
    private List<String> blogImagesBase64;
    private Integer likeCount;
    private Boolean hasLiked;

    // Constructors, getters, and setters

    public BlogRequest() {}

    // Getters and setters for all fields
    // Example:
    public Integer getBlogId() { return blogId; }
    public void setBlogId(Integer blogId) { this.blogId = blogId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getBlogTitle() { return blogTitle; }
    public void setBlogTitle(String blogTitle) { this.blogTitle = blogTitle; }

    public String getBlogContent() { return blogContent; }
    public void setBlogContent(String blogContent) { this.blogContent = blogContent; }

    public String getBlogDate() { return blogDate; }
    public void setBlogDate(String blogDate) { this.blogDate = blogDate; }

    public Double getBlogRate() { return blogRate; }
    public void setBlogRate(Double blogRate) { this.blogRate = blogRate; }

    public Integer getBlogLike() { return blogLike; }
    public void setBlogLike(Integer blogLike) { this.blogLike = blogLike; }

    public Integer getBlogStatus() { return blogStatus; }
    public void setBlogStatus(Integer blogStatus) { this.blogStatus = blogStatus; }

    public String getEateryLocationDetail() { return eateryLocationDetail; }
    public void setEateryLocationDetail(String eateryLocationDetail) { this.eateryLocationDetail = eateryLocationDetail; }

    public String getEateryAddressDetail() { return eateryAddressDetail; }
    public void setEateryAddressDetail(String eateryAddressDetail) { this.eateryAddressDetail = eateryAddressDetail; }

    public Integer getFoodQualityRate() { return foodQualityRate; }
    public void setFoodQualityRate(Integer foodQualityRate) { this.foodQualityRate = foodQualityRate; }

    public Integer getEnvironmentRate() { return environmentRate; }
    public void setEnvironmentRate(Integer environmentRate) { this.environmentRate = environmentRate; }

    public Integer getServiceRate() { return serviceRate; }
    public void setServiceRate(Integer serviceRate) { this.serviceRate = serviceRate; }

    public Integer getPricingRate() { return pricingRate; }
    public void setPricingRate(Integer pricingRate) { this.pricingRate = pricingRate; }

    public Integer getHygieneRate() { return hygieneRate; }
    public void setHygieneRate(Integer hygieneRate) { this.hygieneRate = hygieneRate; }

    public List<String> getFoodTypeNames() { return foodTypeNames; }
    public void setFoodTypeNames(List<String> foodTypeNames) { this.foodTypeNames = foodTypeNames; }

    public List<String> getMealTypeNames() { return mealTypeNames; }
    public void setMealTypeNames(List<String> mealTypeNames) { this.mealTypeNames = mealTypeNames; }

    public List<String> getPriceRanges() { return priceRanges; }
    public void setPriceRanges(List<String> priceRanges) { this.priceRanges = priceRanges; }

    public String getBlogBillImageBase64() { return blogBillImageBase64; }
    public void setBlogBillImageBase64(String blogBillImageBase64) { this.blogBillImageBase64 = blogBillImageBase64; }

    public List<String> getBlogImagesBase64() { return blogImagesBase64; }
    public void setBlogImagesBase64(List<String> blogImagesBase64) { this.blogImagesBase64 = blogImagesBase64; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Boolean getHasLiked() { return hasLiked; }
    public void setHasLiked(Boolean hasLiked) { this.hasLiked = hasLiked; }
}