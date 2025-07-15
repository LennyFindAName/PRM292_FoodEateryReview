package prm392.project.repo;

import android.content.Context;

import prm392.project.factory.APIClient;
import prm392.project.inter.BlogService;
import prm392.project.model.Blog;
import prm392.project.model.DTOs.BlogRequest;
import prm392.project.model.Food;
import retrofit2.Call;
import retrofit2.Retrofit;

public class BlogRepository {
    private BlogService blogService;

    public BlogRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        blogService = retrofit.create(BlogService.class);
    }

    public static BlogService getBlogService(Context context) {
        return APIClient.getClient(context).create(BlogService.class);
    }

    public Call<Blog> getBlogDetails(String blogId) {
        return blogService.getBlogDetails(blogId);
    }

    public Call<Void> createBlog(BlogRequest blog) {
        return blogService.createBlog(blog);
    }
}
