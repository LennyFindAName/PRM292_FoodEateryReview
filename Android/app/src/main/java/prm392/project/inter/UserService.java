package prm392.project.inter;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import prm392.project.model.User;
import prm392.project.model.DTOs.UpdateProfileDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UserService {
    @GET("Profile/getUser")
    Call<User> getCurrentUser();

    @POST("Profile/auth/local/logout")
    Call<Boolean> logout();

    @POST("Profile/UpdateProfile")
    Call<Void> updateUserProfile(@Body UpdateProfileDTO model);
}
