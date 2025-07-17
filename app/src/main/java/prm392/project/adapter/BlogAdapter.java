package prm392.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import prm392.project.R;
import prm392.project.model.Blog;
import prm392.project.view.BlogDetailActivity;
import prm392.project.view.FoodDetailActivity;

public class BlogAdapter extends BaseAdapter {
    private Context context;
    private List<Blog> blogList;


    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @Override
    public int getCount() {
        return blogList.size();
    }

    @Override
    public Object getItem(int i) {
        return blogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_food_card, viewGroup, false);
        }
        Blog blog = blogList.get(i);

        ImageView imageView = view.findViewById(R.id.foodImage);
        TextView nameView = view.findViewById(R.id.foodName);
//        TextView likesView = view.findViewById(R.id.likesView);
        TextView descriptionView = view.findViewById(R.id.description);
        TextView dateView = view.findViewById(R.id.dateView);
        ImageButton addButton = view.findViewById(R.id.btnAddToCart);

        // Set blog data to the UI elements
        nameView.setText(blog.getBlogTitle() != null ? blog.getBlogTitle() : "No Title");

//        String likesText = blog.getBlogLike() != null ? blog.getBlogLike() + " likes" : "0 likes";
//        likesView.setText(likesText);

        // Show username in description field
        descriptionView.setText("By: " + (blog.getUsername() != null ? blog.getUsername() : "Unknown"));

        String dateText = "No Date";
        if (blog.getBlogDate() != null && !blog.getBlogDate().isEmpty()) {
            try {
                SimpleDateFormat inputFormat;
                // Check if the date contains time (T) or just date
                if (blog.getBlogDate().contains("T")) {
                    // Handle full datetime format "2025-07-01T00:00:00"
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    // Handle date-only format "2025-07-08"
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                }

                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                Date date = inputFormat.parse(blog.getBlogDate());
                if (date != null) {
                    dateText = outputFormat.format(date);
                }
            } catch (ParseException e) {
                dateText = "Invalid Date";
            }
        }
        // Format date to Vietnamese format: dd/MM/yyyy
        dateView.setText(dateText);

        // Load image from Base64 string or use default
        String imageToDisplay = null;

        // Use first image from blogImages List if available, otherwise fall back to firstImage
        if (blog.getBlogImages() != null && !blog.getBlogImages().isEmpty()) {
            imageToDisplay = blog.getBlogImages().get(0); // Get first image from the list
        } else if (blog.getFirstImage() != null && !blog.getFirstImage().isEmpty()) {
            imageToDisplay = blog.getFirstImage(); // Fallback to single image
        }

        if (imageToDisplay != null && !imageToDisplay.isEmpty()) {
            try {
                // Remove data URL prefix if present
                String base64String = imageToDisplay;
                if (base64String.contains(",")) {
                    base64String = base64String.substring(base64String.indexOf(",") + 1);
                }

                // Decode Base64 string to bitmap
                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    imageView.setImageBitmap(decodedByte);
                } else {
                    imageView.setImageResource(R.drawable.salah);
                }
            } catch (IllegalArgumentException e) {
                imageView.setImageResource(R.drawable.salah);
            }
        } else {
            imageView.setImageResource(R.drawable.salah);
        }

        // Handle click events , FOR BLOG DETAILS ACTIVITY
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlogDetailActivity.class);
            intent.putExtra("blog_id", String.valueOf(blog.getBlogId()));
            context.startActivity(intent);
        });

        // Hide or modify the add button for blogs
        //addButton.setVisibility(View.GONE); // Hide since it's not relevant for blogs

        return view;
    }
}
