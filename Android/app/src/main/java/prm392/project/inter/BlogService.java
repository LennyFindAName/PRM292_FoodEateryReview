package prm392.project.inter;

import java.util.List;

import prm392.project.model.Blog;
import prm392.project.model.DTOs.BlogRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface BlogService {

    @GET("blog/list")
    Call<List<Blog>> getBlogList(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize
    );

    @GET("blog/detailsApp/{id}")
    Call<Blog> getBlogDetails(@Path("id") String blogId);

    @POST("blog/create")
    Call<Void> createBlog(@Body BlogRequest blog);
}
