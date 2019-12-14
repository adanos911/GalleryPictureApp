package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ayanot.discoveryourfantasy.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.adapter.SpacesItemDecoration;
import com.ayanot.discoveryourfantasy.dataBase.cache.ImageDatabase;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>Абстрактный класс, содержащий основную логику для фрагмента с recycleVIew,
 * для просмотра галереи изображений</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public abstract class ContentImageFragment extends Fragment
        implements AsyncLoadImgTask.OnTaskCompleted {
    private RecyclerView recyclerView;
    private ImageRecycleAdapter recycleAdapter;
    private List<Image> imageList;

    private ImageDatabase imageDatabase;
    private Handler handler;

    private int pageNumber;
    private int offset;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        pageNumber = 1;
        offset = 0;
        if (imageList == null)
            imageList = new ArrayList<>();
    }

    /**
     * <p>Инициализирует recycleVIew с помощию {@link StaggeredGridLayoutManager},
     * содержащий 2 колонки изображений в вертикальном порядке</p>
     *
     * @param recyclerView -
     */
    protected void initRecycleView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager
                (2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //for fix exception onBind position
        recyclerView.setItemAnimator(null);
//        recyclerView.setItemViewCacheSize(10);

        recycleAdapter = new ImageRecycleAdapter(imageList, recyclerView);

        recycleAdapter.setOnItemClickListener((itemView, position) -> {
            Image image = recycleAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ImageActivity.class);
            intent.putExtra(Image.class.getSimpleName(), image);
            startActivity(intent);
        });
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4, 16));
    }

    /**
     * <p>Устанавливает поведение recycleView при обновлении</p>
     *
     * @param view - {@link SwipeRefreshLayout}
     */
    protected void setRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.contentImageFragment);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkConnection()) {
                getRecycleAdapter().clear();
                offset = 0;
                pageNumber = 1;
            } else
                Toast.makeText(getActivity(), getResources().getString
                        (R.string.toast_network_connection_text), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    protected boolean isNetworkConnection() {
        return new ConnectionDetector(getActivity()).isNetworkConnected();
    }

    /**
     * <p>Метод, устанавливающий поведение, при прокрутке recycleView
     *  Определяеться с помощью listener {@link ImageRecycleAdapter.OnLoadMoreListener},
     *  и реализации его метода
     *  {@link ImageRecycleAdapter.OnLoadMoreListener#onLoadMore()}</p>
     *
     * @param recycleAdapter
     */
    protected abstract void setLoadMoreListener(ImageRecycleAdapter recycleAdapter);

    @Override
    public void onTaskCompleted(List<Image> responseImage) {
        if (pageNumber > 1) {
            imageList.remove(imageList.size() - 1);
            recycleAdapter.notifyItemRemoved(imageList.size());
        }
        for (Image image : responseImage) {
            imageList.add(image);
            handler.post(() -> recycleAdapter.notifyItemInserted(imageList.size()));
        }
        recycleAdapter.setLoaded();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ImageRecycleAdapter getRecycleAdapter() {
        return recycleAdapter;
    }

    public void setRecycleAdapter(ImageRecycleAdapter recycleAdapter) {
        this.recycleAdapter = recycleAdapter;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public ImageDatabase getImageDatabase() {
        return imageDatabase;
    }

    public void setImageDatabase(ImageDatabase imageDatabase) {
        this.imageDatabase = imageDatabase;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
