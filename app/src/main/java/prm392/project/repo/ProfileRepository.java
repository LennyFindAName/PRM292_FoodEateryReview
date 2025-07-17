package prm392.project.repo;

import android.content.Context;

import prm392.project.factory.APIClient;
import prm392.project.inter.ProfileService;
import retrofit2.Call;
import retrofit2.Retrofit;
import prm392.project.model.DTOs.BookmarksResponse;

public class ProfileRepository {
    private ProfileService profileService;

    public ProfileRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        profileService = retrofit.create(ProfileService.class);
    }

    public Call<BookmarksResponse> getBookmarks(int userId, int page, int pageSize) {
        return profileService.getBookmarks(userId, page, pageSize);
    }

    public Call<Void> addBookmark(int userId, int blogId) {
        return profileService.addBookmark(userId, blogId);
    }

    public Call<Void> removeBookmark(int userId, int blogId) {
        return profileService.removeBookmark(userId, blogId);
    }
}