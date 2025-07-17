package prm392.project.inter;

import java.util.List;

import prm392.project.model.ChatHistoryModel;
import prm392.project.model.ChatMessage;
import prm392.project.model.ChatRequest;
import prm392.project.model.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {
    @POST("chat")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);

    @GET("chat/history")
    Call<List<ChatHistoryModel>> getChatHistory();

    @POST("chat/clear-chat")
    Call<Void> clearChat();
}
