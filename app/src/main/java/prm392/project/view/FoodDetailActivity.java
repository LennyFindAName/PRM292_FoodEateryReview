package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import prm392.project.R;
import prm392.project.model.Food;
import prm392.project.model.OrderDetail;
import prm392.project.repo.FoodRepository;
import prm392.project.utils.BottomNavHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {
    private ImageView foodImage;
    private TextView foodName, foodDescription, foodPrice, foodCalories;
    private Button btnAddToCart;
    private FoodRepository foodRepository;
    Food tmpFood = new Food();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_detail);

        // Initialize views
        foodImage = findViewById(R.id.foodImage);
        foodName = findViewById(R.id.foodName);
        foodDescription = findViewById(R.id.foodDescription);
        foodPrice = findViewById(R.id.foodPrice);
        foodCalories = findViewById(R.id.foodCalories);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        foodRepository = new FoodRepository(this);

        String foodId = getIntent().getStringExtra("food_id");

        if (foodId != null && !foodId.isEmpty()) {
            // Call the API using the string foodId
            getFoodDetails(foodId);
//            tmpFood.setFoodID(foodId);
//            tmpFood.setName(foodName.toString());
//            tmpFood.setDescription(foodDescription.toString());
//            tmpFood.setCalories(Integer.parseInt(foodCalories.toString()));
//            tmpFood.setPrice(Double.parseDouble(foodPrice.toString()));
//            tmpFood.setImage(foodImage.toString());
        } else {
            Toast.makeText(this, "Invalid food ID", Toast.LENGTH_SHORT).show();
        }

        btnAddToCart.setOnClickListener(v -> {
            saveToCart(tmpFood);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!prm392.project.utils.JwtUtils.isTokenValid(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void getFoodDetails(String foodId) {
        foodRepository.getFoodDetails(foodId).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Food food = response.body();
                        tmpFood = food;
                        // Set data to views
                        foodName.setText(food.getName());
                        foodDescription.setText(food.getDescription());
                        DecimalFormat formatter = new DecimalFormat("#,###"); // Định dạng số với dấu phẩy
                        String formattedPrice = formatter.format(food.getPrice()) + " VNĐ"; // Thêm đơn vị VNĐ
                        foodPrice.setText(formattedPrice);
                        foodCalories.setText("Calories: " + food.getCalories() + " kcal");

                        // Load image with Glide
                        Glide.with(FoodDetailActivity.this)
                                .load(food.getImage())
                                .into(foodImage);
                    } else {
                        // Log the response body if it's null
                        Log.e("FoodDetailActivity", "Response body is null");
                        Toast.makeText(FoodDetailActivity.this, "No food details available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the HTTP status code and error body
                    Log.e("FoodDetailActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(FoodDetailActivity.this, "Failed to load food details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToCart(Food tmpFood) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        OrderDetail orderDetail = new OrderDetail(tmpFood.getFoodID(), 1, tmpFood.getName(),
                tmpFood.getDescription(), tmpFood.getPrice(), tmpFood.getCalories(), tmpFood.getImage());

        // Chuyển đổi đối tượng Food thành JSON
        Gson gson = new Gson();
        String json = gson.toJson(orderDetail);

        // Lưu vào sharedPreferences với id của món ăn làm key
        editor.putString(tmpFood.getFoodID(), json);
        editor.apply();

        // Thông báo cho người dùng
        Toast.makeText(this, "Add " + tmpFood.getName() + " to cart!", Toast.LENGTH_SHORT).show();
    }
}