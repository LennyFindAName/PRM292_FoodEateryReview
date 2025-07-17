package prm392.project.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import prm392.project.R;
import prm392.project.view.*;

public class BottomNavHelper {
    public static void setup(final Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.nav_home) {
                            activity.startActivity(new Intent(activity, HomeActivity.class));
                            activity.finish();
                            return true;
                        } else if (id == R.id.nav_profile) {
                            activity.startActivity(new Intent(activity, ProfileActivity.class));
                            activity.finish();
                            return true;
                        } else if (id == R.id.nav_create_blog) {
                            activity.startActivity(new Intent(activity, CreateBlogActivity.class));
                            activity.finish();
                            return true;
                        } else if (id == R.id.nav_chat) {
                            activity.startActivity(new Intent(activity, ChatActivity.class));
                            activity.finish();
                            return true;
                        } else if (id == R.id.nav_bookmark) {
                            activity.startActivity(new Intent(activity, BookmarkActivity.class));
                            activity.finish();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
    }
}