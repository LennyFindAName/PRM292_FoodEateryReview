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
import prm392.project.model.Bookmark;
import prm392.project.model.DTOs.BookmarksResponse;
import prm392.project.model.User;
import prm392.project.repo.BlogRepository;
import prm392.project.repo.ProfileRepository;
import prm392.project.model.OrderDetail;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.text.Html;
import android.text.Spanned;
import android.os.Build;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.util.Locale;

public class BlogDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ViewPager2 imageViewPager;
    private LinearLayout pageIndicators;
    private TextView imageCounter;
    private ImageCarouselAdapter imageAdapter;
    private TextView blogName, blogDescription, blogPrice, blogCalories, locationText;
    private SeekBar foodQualityRate, environmentRate, serviceRate, pricingRate, hygieneRate;
    private Button btnBookmark;
    private boolean isBookmarked = false;
    private BlogRepository blogRepository;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
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
        btnBookmark = findViewById(R.id.btnBookmark);
        foodQualityRate = findViewById(R.id.foodQualityRate);
        environmentRate = findViewById(R.id.environmentRate);
        serviceRate = findViewById(R.id.serviceRate);
        pricingRate = findViewById(R.id.pricingRate);
        hygieneRate = findViewById(R.id.hygieneRate);
        locationText = findViewById(R.id.locationText);

        blogRepository = new BlogRepository(this);
        ProfileRepository profileRepository = new ProfileRepository(this);

        UserRepository userRepository = new UserRepository(this);
        userRepository.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(BlogDetailActivity.this, "Failed to get user", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                int userId = Integer.parseInt(response.body().getUserID());
                profileRepository.getBookmarks(userId, 1, 99999).enqueue(new Callback<BookmarksResponse>() {
                    @Override
                    public void onResponse(Call<BookmarksResponse> call, Response<BookmarksResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Bookmark> bookmarks = response.body().getBookmarks();
                            isBookmarked = false;
                            for (Bookmark b : bookmarks) {
                                if (b.getBlogId() == tmpBlog.getBlogId()) {
                                    isBookmarked = true;
                                    break;
                                }
                            }
                            updateBookmarkButton();
                        }
                    }
                    @Override
                    public void onFailure(Call<BookmarksResponse> call, Throwable t) { }
                });

                btnBookmark.setOnClickListener(v -> {
                    if (!isBookmarked) {
                        profileRepository.addBookmark(userId, tmpBlog.getBlogId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    isBookmarked = true;
                                    updateBookmarkButton();
                                    Toast.makeText(BlogDetailActivity.this, "Bookmark added", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) { }
                        });
                    } else {
                        profileRepository.removeBookmark(userId, tmpBlog.getBlogId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    isBookmarked = false;
                                    updateBookmarkButton();
                                    Toast.makeText(BlogDetailActivity.this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) { }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(BlogDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });

        String blogId = getIntent().getStringExtra("blog_id");
        Log.e("dangdeptrai", "dangdeptrai blogid: " + blogId);
        if (blogId != null && !blogId.isEmpty()) {
            getblogDetails(blogId);
        } else {
            Toast.makeText(this, "Invalid blog ID", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);

        // Initialize the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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

    private void updateBookmarkButton() {
        btnBookmark.setText(isBookmarked ? "Bookmarked" : "Add to Bookmark");
        btnBookmark.setEnabled(true);
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

                        blogPrice.setText("Tạo lúc: "+ blog.getBlogDate());
                        blogCalories.setText("Lượt like: " + blog.getBlogLike());
                        foodQualityRate.setProgress(blog.getFoodQualityRate());
                        foodQualityRate.setEnabled(false);
                        environmentRate.setProgress(blog.getEnvironmentRate());
                        environmentRate.setEnabled(false);
                        serviceRate.setProgress(blog.getServiceRate());
                        serviceRate.setEnabled(false);
                        pricingRate.setProgress(blog.getPricingRate());
                        pricingRate.setEnabled(false);
                        hygieneRate.setProgress(blog.getHygieneRate());
                        hygieneRate.setEnabled(false);

                        // Setup image carousel
                        List<String> imageList = new ArrayList<>();

                        // Use blogImages List if available, otherwise fall back to firstImage
                        if (blog.getBlogImages() != null && !blog.getBlogImages().isEmpty()) {
                            imageList.addAll(blog.getBlogImages());
                        } else if (blog.getFirstImage() != null && !blog.getFirstImage().isEmpty()) {
                            imageList.add(blog.getFirstImage());
                        }

                        setupImageCarousel(imageList);

                        // Set location text and geocode the address
                        String locationDetail = blog.getEateryLocationDetail();
                        locationText.setText(locationDetail != null ? locationDetail : "Không có thông tin vị trí");

                        // Geocode the address to get coordinates and show on map
                        if (mMap != null && locationDetail != null && !locationDetail.isEmpty()) {
                            geocodeAndShowLocation(locationDetail);
                        }

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

    // Geocode the address and show the location on the map
    private void geocodeAndShowLocation(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Move camera to the geocoded location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Eatery Location"));
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ trên bản đồ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tìm địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable MyLocation layer if the permission is granted
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Request location permission
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Move camera to the default location (e.g., Sydney) if the blog details are not yet loaded
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34.0, 151.0), 10));
    }

}
