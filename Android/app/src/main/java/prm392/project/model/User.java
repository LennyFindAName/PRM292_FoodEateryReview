package prm392.project.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class User {
    @SerializedName("userID")
    private String UserID;

    @SerializedName("username")
    private String Username;

    @SerializedName("email")
    private String Email;

    @SerializedName("phoneNumber")
    private String PhoneNumber;

    @SerializedName("displayName")
    private String DisplayName;

    @SerializedName("role")
    private String Role;

    @SerializedName("userImage")
    private String UserImage;

    public User(String userID, String username, String email, String phoneNumber, String displayName, String role, String userImage) {
        UserID = userID;
        Username = username;
        Email = email;
        PhoneNumber = phoneNumber;
        DisplayName = displayName;
        Role = role;
        UserImage = userImage;
    }

    public User() {
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }
}
