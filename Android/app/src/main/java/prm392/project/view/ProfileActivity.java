package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import prm392.project.R;
import prm392.project.factory.APIClient;
import prm392.project.inter.UserService;
import prm392.project.model.DTOs.UpdateProfileDTO;
import prm392.project.model.User;
import prm392.project.repo.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView hiddenUserId;
    private EditText profileName, profileEmail, profiledisplayName, profilePhone;
    private CircleImageView profileImageView;
    private UserRepository userRepository;

    private ImageButton editProfileButton;
    private boolean isEditing = false;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private String base64Image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        updateCartCountAtHome();
        // Initialize views
        hiddenUserId = findViewById(R.id.hiddenUserId);

        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);
        profiledisplayName = findViewById(R.id.profile_displayName);
        profileImageView = findViewById(R.id.profileImageView);

        editProfileButton = findViewById(R.id.btn_edit_profile);

        editProfileButton.setOnClickListener(v -> {
            isEditing = !isEditing;

            // Toggle editable state
            profiledisplayName.setEnabled(isEditing);
            profilePhone.setEnabled(isEditing);

            // Optionally, keep email read-only
            profileEmail.setEnabled(false);

            if (isEditing) {
                editProfileButton.setImageResource(android.R.drawable.ic_menu_save);
            } else {
                editProfileButton.setImageResource(android.R.drawable.ic_menu_edit);
                saveUserProfile(); // Save profile when toggling back
            }
        });

        profileImageView.setOnClickListener(v -> {
            if (isEditing) {
                openImageChooser();
            }
        });

        userRepository = new UserRepository(this);
        loadUserProfile();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(ProfileActivity.this, CartListActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_location) {
                    Intent intent = new Intent(ProfileActivity.this, GoogleMapsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_create_blog) {
                    Intent intent = new Intent(ProfileActivity.this, CreateBlogActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }

    private void loadUserProfile() {
        userRepository.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        User user = response.body();
                        Log.d("ProfileActivity", "User Data: " + user.getUsername() + " | " + user.getEmail() + " | " + user.getDisplayName() + " | " + user.getPhoneNumber());
                        // Set user data to views
                        hiddenUserId.setText(String.valueOf(user.getUserID()));

                        profileName.setText(user.getUsername());
                        profileEmail.setText(user.getEmail());
                        profiledisplayName.setText(user.getDisplayName());
                        profilePhone.setText(user.getPhoneNumber());

                        if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(user.getUserImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profileImageView.setImageBitmap(decodedByte); // your ImageView
                            } catch (Exception e) {
                                Log.e("ProfileActivity", "Failed to decode image", e);
                            }
                        }
                    } else {
                        // Log the response body if it's null
                        Log.e("ProfileActivity", "Response body is null");
                        Toast.makeText(ProfileActivity.this, "No user profile available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the HTTP status code and error body
                    Log.e("FoodDetailActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(ProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateCartCount(BottomNavigationView bottomNavigationView, int itemCount) {

    }

    private void updateCartCountAtHome() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        SharedPreferences sharedPreferences = this.getSharedPreferences("cart", Context.MODE_PRIVATE);
        int itemCount = sharedPreferences.getAll().size();
        updateCartCount(bottomNavigationView, itemCount);
    }

    private void saveUserProfile() {
        String phone = profilePhone.getText().toString().trim();
        String displayName = profiledisplayName.getText().toString().trim();
        String userId = hiddenUserId.getText().toString(); // You already stored this earlier

        // Optional: add basic validation
        if (userId.isEmpty() || displayName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create DTO
        UpdateProfileDTO model = new UpdateProfileDTO();
        model.setUserId(userId);
        model.setDisplayName(displayName);
        model.setPhone(phone);
        model.setImageLink(base64Image); // Or set base64 string if you want to send image

        // Call API using JSON
        userRepository.updateUserProfile(model).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Show image in preview
                profileImageView.setImageBitmap(bitmap);

                // Convert to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}