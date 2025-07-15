package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

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
import prm392.project.adapter.OrderAdapter;
import prm392.project.model.Order;
import prm392.project.repo.OrderRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    GridView orderHistoryList;
    ArrayList<Order> orderList;
    OrderAdapter orderAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        swipeRefreshLayout = findViewById(R.id.refresh_layout_order_history);
        orderHistoryList = findViewById(R.id.lvOrderHistory);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        loadOrderData();
        orderHistoryList.setAdapter(orderAdapter);

        // Handle pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshOrderData();  // Your method to refresh data
            swipeRefreshLayout.setRefreshing(false);  // Stop the refresh animation
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(OrderHistoryActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(OrderHistoryActivity.this, CartListActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(OrderHistoryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_location) {
                    Intent intent = new Intent(OrderHistoryActivity.this, GoogleMapsActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_create_blog) {
                    Intent intent = new Intent(OrderHistoryActivity.this, CreateBlogActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }


    private void loadOrderData() {
        // call API
        orderRepository = new OrderRepository(this);
        Call<List<Order>> call = orderRepository.getOrders(1, 999999, "");
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                Log.d("OrderHistoryActivity", "Response received from food service");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("OrderHistoryActivity", "Order data successfully loaded");
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();
                } else {
                    Log.d("OrderHistoryActivity", "Response not successful");
                    Toast.makeText(OrderHistoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("HomeActivity", "API error: " + t.getMessage());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(OrderHistoryActivity.this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Method to refresh data
    private void refreshOrderData() {
        orderList.clear();  // Clear the existing list
        loadOrderData();    // Reload the data
        orderAdapter.notifyDataSetChanged();  // Notify adapter of the data change
    }
}