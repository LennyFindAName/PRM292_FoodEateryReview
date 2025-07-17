package prm392.project.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import prm392.project.R;
import prm392.project.factory.APIClient;
import prm392.project.inter.AuthService;
import prm392.project.model.DTOs.LoginRequest;
import prm392.project.model.DTOs.LoginResponse;
import prm392.project.utils.JwtUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("access_token", null);
        if (token != null && !token.isEmpty()) {
            Log.d("LOGIN_DEBUG", "Đã có token, chuyển sang HomeActivity");
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtPassword);
        edtPassword = findViewById(R.id.edtPasswordConfirm);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("LOGIN_DEBUG", "Username: " + username);
        Log.d("LOGIN_DEBUG", "Password: " + password);

        LoginRequest request = new LoginRequest(username, password);
        AuthService authService = APIClient.getClient(this).create(AuthService.class);
        Call<LoginResponse> call = authService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("LOGIN_DEBUG", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginData = response.body();
                    String token = loginData.getAccessToken();

                  // giải mã JWT
                    JwtUtils jwtUtils = new JwtUtils(token);
                    if (!jwtUtils.isValid()) {
                        Toast.makeText(LoginActivity.this, "Token invalid or cannot be decoded", Toast.LENGTH_SHORT).show();
                        Log.e("JWT", "Token decode failed");
                        return;
                    }
                    String userId = jwtUtils.getUserId();
                    String decodedUsername = jwtUtils.getUsername();
                    String role = jwtUtils.getRole();

                    Log.d("JWT", "UserId: " + userId);
                    Log.d("JWT", "Username: " + decodedUsername);
                    Log.d("JWT", "Role: " + role);

                    // ✅ Lưu vào SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("access_token", token);
                    editor.putString("username", loginData.getUsername());
                    editor.putInt("role_id", loginData.getRoleId());
                    if (userId != null) editor.putString("user_id", userId);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LOGIN_DEBUG", "Login failed - Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_DEBUG", "Network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
