package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.entity.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.entity.adapter.SpacesItemDecoration;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

import java.util.ArrayList;
import java.util.List;

public class ContentImageFragment extends Fragment implements AsyncLoadImgTask.OnTaskCompleted {
    private static final String TAG = "ContentImageFragment";

    RecyclerView recyclerView;
    private static int pageNumber;
    private static int offset;
    protected Handler handler;
    StaggeredGridLayoutManager layoutManager;
    private List<Image> imagesList;
    private ImageRecycleAdapter imageRecycleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_image_fragment, container, false);
        setParameters(view);

        return view;
    }

    private void setParameters(View view) {
        imagesList = new ArrayList<>();
        offset = 0;
        pageNumber = 1;
        handler = new Handler();
        recyclerView = view.findViewById(R.id.recycleView);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemViewCacheSize(20);
//        recyclerView.setDrawingCacheEnabled(true);
//        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        layoutManager.setMeasurementCacheEnabled(true);
        imageRecycleAdapter = new ImageRecycleAdapter(imagesList, recyclerView);
        imageRecycleAdapter.setOnItemClickListener(new ImageRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Image image = imageRecycleAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra(Image.class.getSimpleName(), image);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(imageRecycleAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(4, 16));
        getLoadImg(true);
        imageRecycleAdapter.setOnLoadMoreListener(new ImageRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                imagesList.add(null);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageRecycleAdapter.notifyItemInserted(imagesList.size() - 1);
                    }
                });
                ++pageNumber;
                getLoadImg(false);
            }
        });
    }

    private void getLoadImg(boolean first) {
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(getActivity(), this, offset, first);
        offset += (first ? 8 : 16);
        asyncLoadImgTask.execute("/");
    }

    @Override
    public void onTaskCompleted(List<Image> responseImage) {
        if (pageNumber > 1) {
            imagesList.remove(imagesList.size() - 1);
            imageRecycleAdapter.notifyItemRemoved(imagesList.size());
        }
        for (Image image : responseImage) {
            imagesList.add(image);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageRecycleAdapter.notifyItemInserted(imagesList.size());
                }
            });
        }
        imageRecycleAdapter.setLoaded();
    }
}
