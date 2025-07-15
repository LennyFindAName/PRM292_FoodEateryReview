package prm392.project.utils;

import android.util.Log;

import com.auth0.android.jwt.JWT;

public class JwtUtils {

    private final String tag;
    private final JWT jwt;

    public JwtUtils(String token) {
        this.tag = this.getClass().getSimpleName();  // Thay vì static TAG
        JWT tempJwt = null;
        try {
            tempJwt = new JWT(token);
        } catch (Exception e) {
            Log.e(tag, "Invalid JWT token: " + e.getMessage());
        }
        this.jwt = tempJwt;
    }

    public String getUserId() {
        if (jwt == null) return null;
        try {
            return jwt.getClaim("nameid").asString();
        } catch (Exception e) {
            Log.e(tag, "Failed to get userId: " + e.getMessage());
            return null;
        }
    }

    public String getUsername() {
        if (jwt == null) return null;
        try {
            return jwt.getClaim("unique_name").asString();
        } catch (Exception e) {
            Log.e(tag, "Failed to get username: " + e.getMessage());
            return null;
        }
    }

    public String getRole() {
        if (jwt == null) return null;
        try {
            return jwt.getClaim("role").asString();
        } catch (Exception e) {
            Log.e(tag, "Failed to get role: " + e.getMessage());
            return null;
        }
    }

    public boolean isExpired() {
        if (jwt == null) return true;
        try {
            return jwt.isExpired(10);  // true nếu hết hạn trong vòng 10s tới
        } catch (Exception e) {
            Log.e(tag, "Failed to check expiration: " + e.getMessage());
            return true;
        }
    }

    public boolean isValid() {
        return jwt != null;
    }

}
