package prm392.project.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Blog {
    @SerializedName("blogId")
    private int blogId;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("username")
    private String username;

    @SerializedName("blogTitle")
    private String blogTitle;
    @SerializedName("blogDate")
    private String blogDate; // Changed to String to handle "2025-07-01T00:00:00" and avoid API 26 requirement

    @SerializedName("blogLike")
    private Integer blogLike;
    @SerializedName("firstImage")
    private String firstImage; // Changed to String to handle Base64 from API

    @SerializedName("blogImagesBase64")
    private List<String> blogImages; // Changed to String to handle Base64 from API

    @SerializedName("blogContent")
    private String blogContent;

    @SerializedName("eateryLocationDetail")
    private String eateryLocationDetail;


    // Optional fields that might not be in the response
    private Double blogRate;
    private Integer eateryId;
    private Integer blogStatus;
    private String eateryAddressDetail;
    private Integer foodQualityRate;
    private Integer environmentRate;
    private Integer serviceRate;
    private Integer pricingRate;
    private Integer hygieneRate;
    private String opinion;

    // Constructors
    public Blog() {}

    // Getter and Setter methods
    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getBlogContent() {
        return blogContent;
    }

    public void setBlogContent(String blogContent) {
        this.blogContent = blogContent;
    }

    public String getBlogDate() {
        return blogDate;
    }

    public void setBlogDate(String blogDate) {
        this.blogDate = blogDate;
    }

    public Integer getBlogLike() {
        return blogLike;
    }

    public void setBlogLike(Integer blogLike) {
        this.blogLike = blogLike;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public List<String> getBlogImages() {
        return blogImages;
    }

    public void setBlogImages(List<String> blogImages) {
        this.blogImages = blogImages;
    }

    public Double getBlogRate() {
        return blogRate;
    }

    public void setBlogRate(Double blogRate) {
        this.blogRate = blogRate;
    }

    public Integer getEateryId() {
        return eateryId;
    }

    public void setEateryId(Integer eateryId) {
        this.eateryId = eateryId;
    }

    public Integer getBlogStatus() {
        return blogStatus;
    }

    public void setBlogStatus(Integer blogStatus) {
        this.blogStatus = blogStatus;
    }

    public String getEateryLocationDetail() {
        return eateryLocationDetail;
    }

    public void setEateryLocationDetail(String eateryLocationDetail) {
        this.eateryLocationDetail = eateryLocationDetail;
    }

    public String getEateryAddressDetail() {
        return eateryAddressDetail;
    }

    public void setEateryAddressDetail(String eateryAddressDetail) {
        this.eateryAddressDetail = eateryAddressDetail;
    }

    public Integer getFoodQualityRate() {
        return foodQualityRate;
    }

    public void setFoodQualityRate(Integer foodQualityRate) {
        this.foodQualityRate = foodQualityRate;
    }

    public Integer getEnvironmentRate() {
        return environmentRate;
    }

    public void setEnvironmentRate(Integer environmentRate) {
        this.environmentRate = environmentRate;
    }

    public Integer getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(Integer serviceRate) {
        this.serviceRate = serviceRate;
    }

    public Integer getPricingRate() {
        return pricingRate;
    }

    public void setPricingRate(Integer pricingRate) {
        this.pricingRate = pricingRate;
    }

    public Integer getHygieneRate() {
        return hygieneRate;
    }

    public void setHygieneRate(Integer hygieneRate) {
        this.hygieneRate = hygieneRate;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
}
