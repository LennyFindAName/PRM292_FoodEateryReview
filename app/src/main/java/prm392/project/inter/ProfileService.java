// app/src/main/java/prm392/project/inter/ProfileService.java
package prm392.project.inter;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;
import prm392.project.model.DTOs.BookmarksResponse;

public interface ProfileService {
    @GET("Profile/{id}/bookmarks")
    Call<BookmarksResponse> getBookmarks(
            @Path("id") int userId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @POST("Profile/add-bookmark")
    Call<Void> addBookmark(
            @Query("userId") int userId,
            @Query("blogId") int blogId
    );

    @DELETE("Profile/remove-bookmark")
    Call<Void> removeBookmark(
            @Query("userId") int userId,
            @Query("blogId") int blogId
    );
}