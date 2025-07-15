package prm392.project.repo;

import android.content.Context;
import prm392.project.factory.APIClient;
import prm392.project.inter.AuthService;
import prm392.project.model.DTOs.LoginRequest;
import prm392.project.model.DTOs.LoginResponse;
import prm392.project.model.DTOs.RegisterRequest;
import prm392.project.model.SignIn;
import prm392.project.model.ResponseTokenDTO;
import prm392.project.model.SignUp;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;

public class AuthRepository {
    private AuthService authService;

    public AuthRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        authService = retrofit.create(AuthService.class);
    }

    public Call<LoginResponse> login(@Body LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    public Call<Void> register(@Body RegisterRequest request){
        return authService.register(request);
    }
}
