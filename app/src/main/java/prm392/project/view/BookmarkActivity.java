package prm392.project.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import prm392.project.R;
import prm392.project.adapter.BookmarkAdapter;
import prm392.project.model.DTOs.BookmarksResponse;
import prm392.project.model.User;
import prm392.project.repo.ProfileRepository;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;

public class BookmarkActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private ProfileRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.recyclerViewBookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_bookmark);

        repository = new ProfileRepository(this);
        new Thread(() -> {
            UserRepository userRepository = new UserRepository(this);
            Response<User> userResponse;
            try {
                userResponse = userRepository.getUserProfile().execute();
                if (!userResponse.isSuccessful() || userResponse.body() == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                int userId = Integer.parseInt(userResponse.body().getUserID());
                runOnUiThread(() -> {
                    adapter = new BookmarkAdapter(Collections.emptyList(), repository, userId);
                    recyclerView.setAdapter(adapter);
                    loadBookmarks(userId, 1, 99999);
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void loadBookmarks(int userId, int page, int pageSize) {
        repository.getBookmarks(userId, page, pageSize).enqueue(new Callback<BookmarksResponse>() {
            @Override
            public void onResponse(Call<BookmarksResponse> call, Response<BookmarksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getBookmarks().isEmpty()) {
                        Toast.makeText(BookmarkActivity.this, "No bookmark", Toast.LENGTH_SHORT).show();
                    }
                    adapter.setBookmarks(response.body().getBookmarks());
                } else {
                    Toast.makeText(BookmarkActivity.this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookmarksResponse> call, Throwable t) {
                Toast.makeText(BookmarkActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}