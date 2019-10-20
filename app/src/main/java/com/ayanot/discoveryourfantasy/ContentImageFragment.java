package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Downloader;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentImageFragment extends Fragment implements AsyncLoadImgTask.OnTaskCompleted {
    private static final String TAG = "ContentImageFragment";

    RecyclerView recyclerView;
    public static int pageNumber;
    private static int offset = 0;
    protected Handler handler;
    StaggeredGridLayoutManager layoutManager;
    private List<Image> imagesList;
    private ImageRecycleAdapter imageRecycleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_image_fragment, container, false);
        imagesList = new ArrayList<>();
        pageNumber = 1;
        handler = new Handler();
        recyclerView = view.findViewById(R.id.recycleView);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
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
        recyclerView.addItemDecoration(new SpacesItemDecoration(8, 16));
        getLoadImg(true);
        imageRecycleAdapter.setOnLoadMoreListener(new ImageRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("TEST", "IN LAOD MORE");
                imagesList.add(null);
                imageRecycleAdapter.notifyItemInserted(imagesList.size() - 1);
                ++pageNumber;
                getLoadImg(false);
            }
        });
//        Log.d("TEST", "AFTER LOAD MORE");
//        for (int i =0; i < 100; i++)
//            getLoadImg();
        return view;
    }

    public void getLoadImg(boolean first) {
        offset++;
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(getActivity(), this, pageNumber, offset, first);
        asyncLoadImgTask.execute();
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
        Log.d("TEST", "COMPLETTED + PAGE = " + pageNumber);
        imageRecycleAdapter.setLoaded();
        Log.d("TEST", "PARAM LOAD IN END COMPLETED = " + imageRecycleAdapter.isLoading() +
                "  SIZE LIST = " + imagesList.size());

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        try {
//            new AsyncRequestHref(0).execute("/");
//        } catch (Exception e) {
//            Log.d(TAG, e.getMessage());
//            e.printStackTrace();
//        }
//    }

    class AsyncRequestHref extends AsyncTask<String, Image, List<Image>> {
        private int offset;

        AsyncRequestHref(int offset) {
            this.offset = offset;
        }

        @Override
        protected List<Image> doInBackground(String... paths) {
            try {
                List<Image> images = Downloader.getImages(paths[0], offset, 100);
                imagesList.addAll(images);
                for (Image image : images)
                    publishProgress(image);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServerIOException e) {
                e.printStackTrace();
            }
            return imagesList;
        }

        @Override
        protected void onProgressUpdate(Image... images) {
            imageRecycleAdapter.notifyDataSetChanged();
//            recyclerView.requestLayout();
        }
    }

}
