package com.ayanot.discoveryourfantasy.entity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ayanot.discoveryourfantasy.R;
import com.ayanot.discoveryourfantasy.dataBase.DatabaseAdapter;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.picasso.PicassoFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ImageRecycleAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int visibleThreshold = 10;
    private int[] lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private ConnectionDetector connectionDetector;

    private List<Image> images;
    private OnItemClickListener listener;
    private Context context;
    private Picasso picasso;
    private DatabaseAdapter databaseAdapter;

    public ImageRecycleAdapter(final List<Image> images, RecyclerView recyclerView, DatabaseAdapter databaseAdapter) {
        this.images = images;
        this.databaseAdapter = databaseAdapter;
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)
                    recyclerView.getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = staggeredGridLayoutManager.getItemCount();
                            lastVisibleItem = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                            if (!loading && (totalItemCount <= (lastVisibleItem[1] + visibleThreshold))) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                            if (!connectionDetector.isNetworkConnected())
                                loading = false;
                        }
                    });
        }
    }

    public boolean isLoading() {
        return loading;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        picasso = PicassoFactory.getInstance(context);
        connectionDetector = new ConnectionDetector(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.item_image, parent, false);
            viewHolder = new ImgLoadViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }

        return viewHolder;
    }

    public Image getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return images.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ImgLoadViewHolder) {
            final Image image = images.get(position);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ((ImgLoadViewHolder) holder).imageView.setImageBitmap(bitmap);
                    image.setBitmap(bitmap);
                    Log.d("ALOHA", "POSITION = " + position);
                    if (position < 8) {
                        databaseAdapter.open();
                        image.setId(databaseAdapter.insert(image));
                        databaseAdapter.close();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            ((ImgLoadViewHolder) holder).imageView.setTag(target);
            picasso.load(image.getPreview()).into(target);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        this.loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    class ImgLoadViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ImgLoadViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
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

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        final ProgressBar progressBar;

        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }
}
