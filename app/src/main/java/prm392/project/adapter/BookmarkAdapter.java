package prm392.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import prm392.project.model.Bookmark;
import prm392.project.R;
import prm392.project.repo.ProfileRepository;
import prm392.project.view.BlogDetailActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private final ProfileRepository repository;
    private final int userId;
    private List<Bookmark> bookmarks;

    public BookmarkAdapter(List<Bookmark> bookmarks, ProfileRepository repository, int userId) {
        this.bookmarks = bookmarks;
        this.repository = repository;
        this.userId = userId;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.titleTextView.setText(bookmark.getBlogTitle());

        if (bookmark.getImage() != null) {
            byte[] imageBytes = Base64.decode(bookmark.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_delete); // Use a placeholder image
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, BlogDetailActivity.class);
            String blogId = String.valueOf(bookmark.getBlogId());
            intent.putExtra("blog_id", blogId);
            context.startActivity(intent);
        });

        holder.btnUnbookmark.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                int blogId = bookmarks.get(pos).getBlogId();
                repository.removeBookmark(userId, blogId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            bookmarks.remove(pos);
                            notifyItemRemoved(pos);
                            Toast.makeText(v.getContext(), "Bookmark removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(v.getContext(), "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarks != null ? bookmarks.size() : 0;
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        ImageButton btnUnbookmark;

        BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewBlogTitle);
            imageView = itemView.findViewById(R.id.imageViewBlogImage);
            btnUnbookmark = itemView.findViewById(R.id.btnUnbookmark);
        }
    }
}