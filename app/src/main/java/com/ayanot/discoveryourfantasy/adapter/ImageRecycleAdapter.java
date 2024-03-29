package com.ayanot.discoveryourfantasy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ayanot.discoveryourfantasy.R;
import com.ayanot.discoveryourfantasy.dataBase.cache.AsyncCleaningImageCacheTask;
import com.ayanot.discoveryourfantasy.dataBase.cache.ImageDatabase;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.BitmapHelper;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.picasso.PicassoFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * <h3>Класс-адаптер, отвечающий за помещение объектов {@link Image} в
 * элементы recylceView {@link RecyclerView}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class ImageRecycleAdapter extends RecyclerView.Adapter {
    /**
     * <p>Тип элемента, соответсвующий изображению</p>
     */
    private final int VIEW_ITEM = 1;
    /**
     * <p>Тип элемента, соответсвующий загрузке</p>
     */
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

    private RecyclerView recyclerView;

    public ImageRecycleAdapter(final List<Image> images, RecyclerView recyclerView) {
        this.images = images;
        this.recyclerView = recyclerView;
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager)
                    recyclerView.getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = staggeredGridLayoutManager.getItemCount();
                            lastVisibleItem = staggeredGridLayoutManager.
                                    findLastVisibleItemPositions(null);
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

    public void clear() {
        images.clear();
        notifyDataSetChanged();
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
                    image.setBitmap(BitmapHelper.getBytesArray(bitmap, Bitmap.CompressFormat.JPEG));
                    ((ImgLoadViewHolder) holder).imageView.setImageBitmap(bitmap);
                    if (position < 8) {
                        if (connectionDetector.isNetworkConnected()) {
                            new AsyncSaveImageToDatabase(context).execute(image);
                        }
                    }
                    ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).invalidateSpanAssignments();
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

    /**
     * <h3>Interface definition for a callback to be invoked when a item View
     * is clicked</h3>
     */
    public interface OnItemClickListener {
        /**
         * <p>Called when a item View is clicked</p>
         *
         * @param itemView - item View in recycleView
         * @param position - position for item in recycleView
         */
        void onItemClick(View itemView, int position);
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

    /**
     * <h3>Interface definition for a callback to be invoked when a recycleView
     * is scrolled</h3>
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * <p>Асинхронный процесс, загружающий первые изображения в кеш,
     * для оффлайн запуска приложения</p>
     */
    private static class AsyncSaveImageToDatabase extends AsyncTask<Image, Void, Void> {
        private final WeakReference<Context> contextWeakReference;

        AsyncSaveImageToDatabase(Context context) {
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            new AsyncCleaningImageCacheTask(contextWeakReference.get()).execute();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Image... images) {
            ImageDatabase.getInstance(contextWeakReference.get()).imageDao().insertAll(images);
            return null;
        }
    }

    /**
     * <p>Класс holder для хранения item являющегося progress Bar</p>
     */
    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        final ProgressBar progressBar;

        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    /**
     * <p>Класс holder для хранения item являющегося изображением</p>
     */
    class ImgLoadViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ImgLoadViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            setIsRecyclable(false);
            imageView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onItemClick(itemView, position);
                }
            });
            setIsRecyclable(true);
        }
    }
}
