package prm392.project.model.DTOs;

public class LoginResponse {
    private String accessToken;
    private String username;
    private int roleId;

    public String getAccessToken() { return accessToken; }
    public String getUsername() { return username; }
    public int getRoleId() { return roleId; }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}