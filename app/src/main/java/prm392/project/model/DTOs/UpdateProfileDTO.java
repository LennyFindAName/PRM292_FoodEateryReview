package prm392.project.model.DTOs;

public class UpdateProfileDTO {
    private String userId;
    private String displayName;
    private String phone;
    private String imageLink; // optional

    public UpdateProfileDTO() {
    }

    public UpdateProfileDTO(String userId, String displayName, String phone, String imageLink) {
        this.userId = userId;
        this.displayName = displayName;
        this.phone = phone;
        this.imageLink = imageLink;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
