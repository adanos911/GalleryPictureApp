package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.entity.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.entity.adapter.SpacesItemDecoration;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

import java.util.ArrayList;
import java.util.List;

//TODO: NEED CREATE ABSTRACT CLASS FOR @this and @ContentImageFragment
public class ContentImageFragmentForSearch extends Fragment implements AsyncLoadImgTask.OnTaskCompleted {
    private static int pageNumber;
    private static int offset;
    private RecyclerView recyclerView;
    private Handler handler;
    private StaggeredGridLayoutManager layoutManager;
    private List<Image> imagesList;
    private ImageRecycleAdapter imageRecycleAdapter;
    private ConnectionDetector connectionDetector;
    private String query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_image_fragment, container, false);
        if (getArguments() != null)
            query = "^.*" + getArguments().getString(SearchResultsActivity.class.getSimpleName())
                    + ".*$";
        setParameters(view);

        return view;
    }

    private void setParameters(View view) {
        imagesList = new ArrayList<>();
        offset = 0;
        pageNumber = 1;
        connectionDetector = new ConnectionDetector(getActivity());
        handler = new Handler();
        recyclerView = view.findViewById(R.id.recycleView);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        imageRecycleAdapter = new ImageRecycleAdapter(imagesList, recyclerView, null);
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
        if (connectionDetector.isNetworkConnected())
            getLoadImg();
        imageRecycleAdapter.setOnLoadMoreListener(new ImageRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!connectionDetector.isNetworkConnected())
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }

    private void getLoadImg() {
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(this, offset, pageNumber);
        offset += (pageNumber == 1 ? 8 : 16);
        asyncLoadImgTask.execute("/", query);
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
