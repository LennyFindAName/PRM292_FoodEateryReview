package prm392.project.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import prm392.project.R;
import prm392.project.factory.APIClient;
import prm392.project.inter.AuthService;
import prm392.project.model.DTOs.RegisterRequest;
import prm392.project.model.ResponseTokenDTO;
import prm392.project.model.SignUp;
import prm392.project.repo.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtPasswordConfirm, edtEmail, edtPhone;
    private Button btnSignUp;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhoneNumber);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(v -> registerUser());
        tvSignIn.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private boolean validateForm() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String passwordConfirm = edtPasswordConfirm.getText().toString();

        if (username.isEmpty()) {
            edtUsername.setError("Username is required");
            edtUsername.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email format");
            edtEmail.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return false;
        }
        if (!phone.matches("[0-9]{9,15}")) {
            edtPhone.setError("Invalid phone number");
            edtPhone.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
            return false;
        }
        if (!password.equals(passwordConfirm)) {
            edtPasswordConfirm.setError("Passwords do not match");
            edtPasswordConfirm.requestFocus();
            return false;
        }
        return true;
    }

    private void registerUser() {
        if (!validateForm()) return;
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String passwordConfirm = edtPasswordConfirm.getText().toString();

        RegisterRequest request = new RegisterRequest(username, password, passwordConfirm, email, phone);
        AuthService authService = APIClient.getClient(this).create(AuthService.class);

        authService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("REGISTER_DEBUG", "Response Code: " + response.code());
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("REGISTER_DEBUG", "Error: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
