// Java: app/src/main/java/prm392/project/view/CreateBlogActivity.java
package prm392.project.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import prm392.project.R;
import prm392.project.inter.UserService;
import prm392.project.model.DTOs.BlogRequest;
import prm392.project.model.User;
import prm392.project.repo.BlogRepository;
import prm392.project.repo.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBlogActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText, dateEditText, eateryAddressEditText, eateryLocationEditText,
            foodQualityEditText, environmentEditText, serviceEditText, pricingEditText, hygieneEditText, overallEditText,
            foodTypesEditText, mealTypesEditText, priceRangeEditText, billImageEditText, blogImagesEditText;
    private Button submitButton;
    private BlogRepository blogRepository;
    private static final int PICK_BILL_IMAGE = 1;
    private static final int PICK_BLOG_IMAGE = 2;
    private String billImageBase64 = "";
    private ArrayList<String> blogImagesBase64 = new ArrayList<>();
    private ImageView imageViewBill;
    private LinearLayout layoutBlogImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_create_blog);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.nav_home) {
                            Intent intent = new Intent(CreateBlogActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (item.getItemId() == R.id.nav_cart) {
                            Intent intent = new Intent(CreateBlogActivity.this, CartListActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (item.getItemId() == R.id.nav_profile) {
                            Intent intent = new Intent(CreateBlogActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (item.getItemId() == R.id.nav_location) {
                            Intent intent = new Intent(CreateBlogActivity.this, GoogleMapsActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (item.getItemId() == R.id.nav_create_blog) {
                            Intent intent = new Intent(CreateBlogActivity.this, CreateBlogActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });

        titleEditText = findViewById(R.id.editTextTitle);
        contentEditText = findViewById(R.id.editTextContent);
        eateryAddressEditText = findViewById(R.id.editTextEateryAddress);
        eateryLocationEditText = findViewById(R.id.editTextEateryLocation);
        foodQualityEditText = findViewById(R.id.editTextFoodQuality);
        environmentEditText = findViewById(R.id.editTextEnvironment);
        serviceEditText = findViewById(R.id.editTextService);
        pricingEditText = findViewById(R.id.editTextPricing);
        hygieneEditText = findViewById(R.id.editTextHygiene);
        overallEditText = findViewById(R.id.editTextOverall);
        foodTypesEditText = findViewById(R.id.editTextFoodTypes);
        mealTypesEditText = findViewById(R.id.editTextMealTypes);
        priceRangeEditText = findViewById(R.id.editTextPriceRange);
        submitButton = findViewById(R.id.buttonSubmit);

        Button buttonSelectBillImage = findViewById(R.id.buttonSelectBillImage);
        Button buttonSelectBlogImages = findViewById(R.id.buttonSelectBlogImages);
        imageViewBill = findViewById(R.id.imageViewBill);
        layoutBlogImages = findViewById(R.id.layoutBlogImages);

        buttonSelectBillImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_BILL_IMAGE);
        });

        buttonSelectBlogImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_BLOG_IMAGE);
        });

        submitButton.setOnClickListener(v -> {
            submitButton.setEnabled(false);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    BlogRequest blogRequest = new BlogRequest();
                    BlogRepository blogRepository = new BlogRepository(this);
                    blogRequest.setBlogTitle(titleEditText.getText().toString().trim());
                    blogRequest.setBlogContent(contentEditText.getText().toString().trim());

                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = outputFormat.format(Calendar.getInstance().getTime());
                    blogRequest.setBlogDate(formattedDate);

                    blogRequest.setEateryAddressDetail(eateryAddressEditText.getText().toString().trim());
                    blogRequest.setEateryLocationDetail(eateryLocationEditText.getText().toString().trim());
                    blogRequest.setFoodQualityRate(parseInt(foodQualityEditText.getText().toString()));
                    blogRequest.setEnvironmentRate(parseInt(environmentEditText.getText().toString()));
                    blogRequest.setServiceRate(parseInt(serviceEditText.getText().toString()));
                    blogRequest.setPricingRate(parseInt(pricingEditText.getText().toString()));
                    blogRequest.setHygieneRate(parseInt(hygieneEditText.getText().toString()));
                    blogRequest.setBlogRate(parseDouble(overallEditText.getText().toString()));
                    blogRequest.setFoodTypeNames(splitList(foodTypesEditText.getText().toString()));
                    blogRequest.setMealTypeNames(splitList(mealTypesEditText.getText().toString()));
                    blogRequest.setPriceRanges(splitList(priceRangeEditText.getText().toString()));
                    blogRequest.setBlogBillImageBase64(billImageBase64);
                    blogRequest.setBlogImagesBase64(blogImagesBase64);
                    blogRequest.setBlogLike(0);
                    blogRequest.setBlogStatus(0);
                    blogRequest.setLikeCount(0);
                    blogRequest.setHasLiked(false);

//                    UserRepository userRepository = new UserRepository(this);
//                    Response<User> userResponse = userRepository.getUserProfile().execute();
//                    if (!userResponse.isSuccessful() || userResponse.body() == null) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
//                            finish();
//                        });
//                        return;
//                    }

                    // Temporarily using a mock user ID
                    int userId = 1;
                    blogRequest.setUserId(userId);

                    if (blogRequest.getUserId() == null ||
                            blogRequest.getBlogTitle().isEmpty() ||
                            blogRequest.getBlogContent().isEmpty() ||
                            blogRequest.getBlogBillImageBase64().isEmpty()){
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                            submitButton.setEnabled(true);
                        });
                        return;
                    }

                    Gson gson = new Gson();
                    String jsonPayload = gson.toJson(blogRequest);
                    System.out.println("BlogRequest JSON payload: " + jsonPayload);
                    android.util.Log.d("CreateBlogActivity", "BlogRequest JSON payload: " + jsonPayload);

                    Response<Void> blogResponse = blogRepository.createBlog(blogRequest).execute();
                    runOnUiThread(() -> {
                        if (blogResponse.isSuccessful()) {
                            Toast.makeText(this, "Blog created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to create blog", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        });
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private ArrayList<String> splitList(String s) {
        if (s.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_BILL_IMAGE && data.getData() != null) {
                Uri uri = data.getData();
                billImageBase64 = encodeImageToBase64(uri);
                imageViewBill.setImageURI(uri);
            } else if (requestCode == PICK_BLOG_IMAGE) {
                blogImagesBase64.clear();
                layoutBlogImages.removeAllViews();
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        blogImagesBase64.add(encodeImageToBase64(uri));
                        addImageToLayout(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    blogImagesBase64.add(encodeImageToBase64(uri));
                    addImageToLayout(uri);
                }
            }
        }
    }

    private void addImageToLayout(Uri uri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(uri);
        layoutBlogImages.addView(imageView);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }
}
