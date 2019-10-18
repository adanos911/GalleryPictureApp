package com.ayanot.discoveryourfantasy.entity;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ayanot.discoveryourfantasy.MainActivity;
import com.ayanot.discoveryourfantasy.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class ImageRecycleAdapter extends RecyclerView.Adapter<ImageRecycleAdapter.ViewHolder> {

    private List<Image> images;
    private OnItemClickListener listener;
    private Context context;

    public ImageRecycleAdapter(List<Image> images) {
        this.images = images;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, parent, false);

        return new ViewHolder(view);
    }

    private static Picasso getImageLoader(Context ctx) {
        Picasso.Builder builder = new Picasso.Builder(ctx);

        builder.downloader(new UrlConnectionDownloader(ctx) {
            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                HttpURLConnection connection = super.openConnection(uri);
                connection.setRequestProperty("Authorization", "OAuth " + MainActivity.TOKEN);
                return connection;
            }
        });
        return builder.build();
    }

    public Image getItem(int position) {
        return images.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);
        ImageView imageView = holder.imageView;

        getImageLoader(context).load(image.getPreview())
                .into(imageView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItem);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}
