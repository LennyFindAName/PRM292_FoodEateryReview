package prm392.project.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import prm392.project.R;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {
    private List<String> imageList;

    public ImageCarouselAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String base64Image = imageList.get(position);

        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                // Remove data URL prefix if present
                String base64String = base64Image;
                if (base64String.contains(",")) {
                    base64String = base64String.substring(base64String.indexOf(",") + 1);
                }

                byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    holder.imageView.setImageBitmap(decodedByte);
                    Log.d("ImageCarouselAdapter", "Image " + position + " loaded successfully");
                } else {
                    Log.e("ImageCarouselAdapter", "Failed to decode bitmap for image " + position);
                    holder.imageView.setImageResource(R.drawable.salah);
                }
            } catch (Exception e) {
                Log.e("ImageCarouselAdapter", "Error loading image " + position + ": " + e.getMessage());
                holder.imageView.setImageResource(R.drawable.salah);
            }
        } else {
            holder.imageView.setImageResource(R.drawable.salah);
        }
    }

    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }

    public void updateImages(List<String> newImageList) {
        this.imageList = newImageList;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image);
        }
    }
}
