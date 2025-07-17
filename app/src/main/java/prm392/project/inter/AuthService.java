package prm392.project.inter;

import prm392.project.model.DTOs.LoginRequest;
import prm392.project.model.DTOs.LoginResponse;
import prm392.project.model.DTOs.RegisterRequest;
import prm392.project.model.SignIn;
import prm392.project.model.ResponseTokenDTO;
import prm392.project.model.SignUp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
//    @POST("User/auth/local/signin")
//    Call<ResponseTokenDTO> login(@Body SignIn user);

    @POST("Authen/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("Authen/register")
    Call<Void> register(@Body RegisterRequest request);
}
