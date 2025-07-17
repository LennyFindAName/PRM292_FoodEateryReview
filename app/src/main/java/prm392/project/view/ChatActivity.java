package prm392.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import java.io.IOException;

import prm392.project.R;
import prm392.project.inter.ChatService;
import prm392.project.model.ChatRequest;
import prm392.project.model.ChatResponse;
import prm392.project.model.ChatHistoryModel;
import prm392.project.model.User;
import prm392.project.model.MessageModel;
import prm392.project.repo.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.ImageButton;
import prm392.project.utils.BottomNavHelper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton backButton;
    private ImageButton clearChatButton;
    private UserRepository userRepository;
    private ChatService chatService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);

        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);
        clearChatButton = findViewById(R.id.clear_chat_button);
        userRepository = new UserRepository(this);
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        loadUserProfile();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_chat);

        // Sử dụng APIClient để tạo ChatService
        chatService = prm392.project.factory.APIClient.getClient(this).create(ChatService.class);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        clearChatButton.setOnClickListener(view -> {
            chatService.clearChat().enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        messageList.clear();
                        messageAdapter.notifyDataSetChanged();
                        Toast.makeText(ChatActivity.this, "Chat history cleared", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to clear chat history", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendUserMessage(message);
                messageInput.setText("");
            }
        });
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

    private void sendUserMessage(String message) {
        // Thêm tin nhắn user vào list
        MessageModel userMsg = new MessageModel(message, "Bạn", false, System.currentTimeMillis());
        messageList.add(userMsg);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
        // Hiển thị hiệu ứng bot đang trả lời (nếu muốn, có thể thêm item typing vào list)
        // Gửi request lên server
        ChatRequest chatRequest = new ChatRequest(message);
        chatService.sendMessage(chatRequest).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageModel botMsg = new MessageModel(response.body().getResponse(), "Chatbot", true, System.currentTimeMillis());
                    messageList.add(botMsg);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    scrollToBottom();
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        int itemCount = messageAdapter.getItemCount();
        if (itemCount > 0) {
            recyclerViewMessages.post(() -> recyclerViewMessages.smoothScrollToPosition(itemCount - 1));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadUserProfile() {
        userRepository.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        User user = response.body();
                        currentUser = user;
                        loadChatHistory();
                    } else {
                        Log.e("ChatActivity", "Response body is null");
                        Toast.makeText(ChatActivity.this, "No user profile available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ChatActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(ChatActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatHistory() {
        chatService.getChatHistory().enqueue(new Callback<List<ChatHistoryModel>>() {
            @Override
            public void onResponse(Call<List<ChatHistoryModel>> call, Response<List<ChatHistoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatHistoryModel> chatList = response.body();
                    java.util.Collections.reverse(chatList); // Đảo ngược thứ tự lịch sử
                    messageList.clear();
                    for (ChatHistoryModel chat : chatList) {
                        messageList.add(new MessageModel(chat.getMessage(), "Bạn", false, System.currentTimeMillis()));
                        messageList.add(new MessageModel(chat.getResponse(), "Chatbot", true, System.currentTimeMillis()));
                    }
                    messageAdapter.notifyDataSetChanged();
                    scrollToBottom();
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to load chat history", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<ChatHistoryModel>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
