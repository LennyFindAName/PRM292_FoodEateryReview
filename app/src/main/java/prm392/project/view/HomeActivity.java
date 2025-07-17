package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import prm392.project.R;
import prm392.project.adapter.BlogAdapter;
import prm392.project.adapter.FoodAdapter;
import prm392.project.inter.OnCartUpdateListener;
import prm392.project.model.DTOs.BlogPagedResponse;
import prm392.project.repo.BlogRepository;
import prm392.project.inter.FoodService;
import prm392.project.inter.BlogService;

import prm392.project.model.Food;
import prm392.project.model.Blog;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnCartUpdateListener {

    GridView gridView;
    //FoodAdapter foodAdapter;

    BlogAdapter blogAdapter;
    //ArrayList<Food> foodList;

    ArrayList<Blog> blogList;
    ArrayList<Blog> allBlogList; // Store all blogs for filtering
    SwipeRefreshLayout swipeRefreshLayout;
    EditText searchEditText;
    //FoodService foodService;
    BlogService blogService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "onCreate: Activity is being created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        blogList =  new ArrayList<>(); // Initialize the blogList
        allBlogList = new ArrayList<>(); // Initialize the allBlogList
        //foodList = new ArrayList<>(); // Initialize the foodList
        Log.d("HomeActivity", "Food list initialized");

        //foodService = FoodRepository.getFoodService(this); // Initialize foodService
        //foodAdapter = new FoodAdapter(this, foodList, this);

        //For blogs
        blogService = BlogRepository.getBlogService(this); // Initialize blogService
        blogAdapter  = new BlogAdapter(this, blogList);

        gridView = findViewById(R.id.foodListView);
        gridView.setAdapter(blogAdapter);

        Log.d("HomeActivity", "Food adapter set for GridView");


        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.logout) {
                        Log.d("HomeActivity", "Logout menu item clicked");
                        // Xóa token và chuyển về MainActivity
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("access_token");
                        editor.apply();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (item.getItemId() == R.id.orderHistory) {
                        Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                        startActivity(intent);
                    }
                    else if (item.getItemId() == R.id.chat){
                        // Mở ChatActivity, đổi tên hiển thị là Chat with AI
                        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                        intent.putExtra("chatType", "AI");
                        startActivity(intent);
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Initialize search EditText and set up search functionality
        searchEditText = findViewById(R.id.search_edit_text);
        setupSearchFunctionality();

        loadBlogData();
        Log.d("HomeActivity", "Food data loading started");

        // Handle pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("HomeActivity", "Pull-to-refresh triggered");
            refreshBlogData();  // Your method to refresh data
            swipeRefreshLayout.setRefreshing(false);  // Stop the refresh animation
            Log.d("HomeActivity", "Pull-to-refresh completed");
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if (!prm392.project.utils.JwtUtils.isTokenValid(this)) {
        //     Intent intent = new Intent(this, LoginActivity.class);
        //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     startActivity(intent);
        //     finish();
        // }
    }

    @Override
    public void onCartUpdated(int itemCount) {
        // CartSize functionality removed - no longer needed
    }

    // Method to load the initial data
    private void loadBlogData() {
        updateCartCountAtHome();
        Log.d("HomeActivity", "Loading food data...");
        //Call<List<Food>> call = foodService.getFoodList(1, 99999, "");
        Call<BlogPagedResponse> blogCall = blogService.getPagedBlogs (1, 99999);

        blogCall.enqueue(new Callback<BlogPagedResponse>() {
            @Override
            public void onResponse(Call<BlogPagedResponse> call, Response<BlogPagedResponse> response) {
                Log.d("HomeActivity", "Response received from blog service");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeActivity", "Blog data successfully loaded");
                    blogList.clear();
                    blogList.addAll(response.body().blogs);
                    allBlogList.clear(); // Clear the allBlogList
                    allBlogList.addAll(response.body().blogs); // Add all blogs to allBlogList
                    blogAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeActivity", "Failed to load blog data: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load blogs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BlogPagedResponse> call, Throwable t) {
                Log.e("HomeActivity", "API error: " + t.getMessage());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(HomeActivity.this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* call.enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                Log.d("HomeActivity", "Response received from food service");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeActivity", "Food data successfully loaded");
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeActivity", "Failed to load food data: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Log.e("HomeActivity", "API error: " + t.getMessage());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(HomeActivity.this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/




    }

    // Method to refresh data
    private void refreshBlogData() {
        Log.d("HomeActivity", "Refreshing food data...");
        //foodList.clear();  // Clear the existing list
        blogList.clear();  // Clear the existing blog list

        loadBlogData();    // Reload the data
       // foodAdapter.notifyDataSetChanged();  // Notify adapter of the data change
        blogAdapter.notifyDataSetChanged();  // Notify blog adapter of the data change

        Log.d("HomeActivity", "Food data refreshed");
    }


    private void updateCartCountAtHome() {
        // CartSize functionality removed - no longer needed
    }

    // Setup search functionality with TextWatcher
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter blogs in real-time as user types
                filterBlogs(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });
    }

    // Filter blogs based on search query
    private void filterBlogs(String searchQuery) {
        Log.d("HomeActivity", "Filtering blogs with query: " + searchQuery);

        blogList.clear(); // Clear current displayed list

        if (searchQuery.isEmpty()) {
            // If search query is empty, show all blogs
            blogList.addAll(allBlogList);
        } else {
            // Filter blogs based on blog title (case-insensitive)
            for (Blog blog : allBlogList) {
                if (blog.getBlogTitle() != null &&
                    blog.getBlogTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                    blogList.add(blog);
                }
            }
        }

        // Notify adapter of data changes
        blogAdapter.notifyDataSetChanged();

        Log.d("HomeActivity", "Filtered results: " + blogList.size() + " blogs found");
    }

}