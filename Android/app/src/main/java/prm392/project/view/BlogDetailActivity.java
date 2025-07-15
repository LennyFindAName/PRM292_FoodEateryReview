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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import prm392.project.R;
import prm392.project.adapter.ImageCarouselAdapter;
import prm392.project.model.Blog;
import prm392.project.repo.BlogRepository;
import prm392.project.model.OrderDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.text.Html;
import android.text.Spanned;
import android.os.Build;

public class BlogDetailActivity extends AppCompatActivity {
    private ViewPager2 imageViewPager;
    private LinearLayout pageIndicators;
    private TextView imageCounter;
    private ImageCarouselAdapter imageAdapter;
    private TextView blogName, blogDescription, blogPrice, blogCalories;
    private SeekBar foodQualityRate, environmentRate, serviceRate, pricingRate, hygieneRate;
    private Button btnAddToCart;
    private BlogRepository blogRepository;
    Blog tmpBlog = new Blog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blog_detail);

        // Initialize views
        imageViewPager = findViewById(R.id.imageViewPager);
        pageIndicators = findViewById(R.id.pageIndicators);
        imageCounter = findViewById(R.id.imageCounter);
        blogName = findViewById(R.id.blogName);
        blogDescription = findViewById(R.id.blogDescription);
        blogPrice = findViewById(R.id.blogPrice);
        blogCalories = findViewById(R.id.blogCalories);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        foodQualityRate = findViewById(R.id.foodQualityRate);
        environmentRate = findViewById(R.id.environmentRate);
        serviceRate = findViewById(R.id.serviceRate);
        pricingRate = findViewById(R.id.pricingRate);
        hygieneRate = findViewById(R.id.hygieneRate);

        blogRepository = new BlogRepository(this);

        String blogId = getIntent().getStringExtra("blog_id");
        Log.e("dangdeptrai", "dangdeptrai blogid: " + blogId);
        if (blogId != null && !blogId.isEmpty()) {
            getblogDetails(blogId);
        } else {
            Toast.makeText(this, "Invalid blog ID", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(BlogDetailActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(BlogDetailActivity.this, CartListActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(BlogDetailActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_location) {
                    Intent intent = new Intent(BlogDetailActivity.this, GoogleMapsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_create_blog) {
                    Intent intent = new Intent(BlogDetailActivity.this, CreateBlogActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

    }

    private void setupImageCarousel(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            // Show placeholder image
            List<String> placeholderList = new ArrayList<>();
            placeholderList.add(""); // Empty string will show fallback image
            imageList = placeholderList;
        }

        final List<String> finalImageList = imageList; // Make it effectively final
        imageAdapter = new ImageCarouselAdapter(finalImageList);
        imageViewPager.setAdapter(imageAdapter);

        // Setup page indicators
        setupPageIndicators(finalImageList.size());

        // Setup image counter
        updateImageCounter(1, finalImageList.size());

        // Setup ViewPager2 page change callback
        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePageIndicators(position);
                updateImageCounter(position + 1, finalImageList.size());
            }
        });
    }

    private void setupPageIndicators(int count) {
        pageIndicators.removeAllViews();

        if (count <= 1) {
            pageIndicators.setVisibility(View.GONE);
            return;
        }

        pageIndicators.setVisibility(View.VISIBLE);

        for (int i = 0; i < count; i++) {
            View indicator = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(android.R.dimen.app_icon_size) / 4,
                    getResources().getDimensionPixelSize(android.R.dimen.app_icon_size) / 4
            );
            params.setMargins(4, 0, 4, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(i == 0 ? R.drawable.indicator_active : R.drawable.indicator_inactive);
            pageIndicators.addView(indicator);
        }
    }

    private void updatePageIndicators(int selectedPosition) {
        for (int i = 0; i < pageIndicators.getChildCount(); i++) {
            View indicator = pageIndicators.getChildAt(i);
            indicator.setBackgroundResource(
                    i == selectedPosition ? R.drawable.indicator_active : R.drawable.indicator_inactive
            );
        }
    }

    private void updateImageCounter(int current, int total) {
        if (total <= 1) {
            imageCounter.setVisibility(View.GONE);
        } else {
            imageCounter.setVisibility(View.VISIBLE);
            imageCounter.setText(current + "/" + total);
        }
    }

    // Helper method to convert HTML content to formatted text
    private Spanned parseHtmlContent(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return Html.fromHtml("", Html.FROM_HTML_MODE_LEGACY);
        }

        // Clean up common HTML entities and improve formatting
        String cleanedHtml = htmlContent
                .replace("&nbsp;", " ")  // Non-breaking space
                .replace("&amp;", "&")   // Ampersand
                .replace("&lt;", "<")    // Less than
                .replace("&gt;", ">")    // Greater than
                .replace("&quot;", "\"") // Quote
                .trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(cleanedHtml, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(cleanedHtml);
        }
    }

    private void getblogDetails(String blogId) {
        if (blogId == null || blogId.isEmpty()) {
            Log.e("blogDetailActivity", "Invalid blog ID");
            Toast.makeText(this, "Invalid blog ID", Toast.LENGTH_SHORT).show();
            return;
        }

        blogRepository.getBlogDetails(blogId).enqueue(new Callback<Blog>() {
            @Override
            public void onResponse(Call<Blog> call, Response<Blog> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Blog blog = response.body();
                        tmpBlog = blog;
                        // Set data to views
                        blogName.setText(blog.getBlogTitle());

                        // Parse HTML content and display it with proper formatting
                        Spanned formattedContent = parseHtmlContent(blog.getBlogContent());
                        blogDescription.setText(formattedContent);

                        blogPrice.setText(blog.getBlogDate());
                        blogCalories.setText("Lượt like: " + blog.getBlogLike());
                        foodQualityRate.setProgress(blog.getFoodQualityRate());
                        environmentRate.setProgress(blog.getEnvironmentRate());
                        serviceRate.setProgress(blog.getServiceRate());
                        pricingRate.setProgress(blog.getPricingRate());
                        hygieneRate.setProgress(blog.getHygieneRate());

                        // Setup image carousel
                        List<String> imageList = new ArrayList<>();

                        // Use blogImages List if available, otherwise fall back to firstImage
                        if (blog.getBlogImages() != null && !blog.getBlogImages().isEmpty()) {
                            imageList.addAll(blog.getBlogImages());
                        } else if (blog.getFirstImage() != null && !blog.getFirstImage().isEmpty()) {
                            imageList.add(blog.getFirstImage());
                        }

                        setupImageCarousel(imageList);

                    } else {
                        // Log the response body if it's null
                        Log.e("blogDetailActivity", "Response body is null");
                        Toast.makeText(BlogDetailActivity.this, "No blog details available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the HTTP status code and error body
                    Log.e("blogDetailActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(BlogDetailActivity.this, "Failed to load blog details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Blog> call, Throwable t) {
                Toast.makeText(BlogDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}