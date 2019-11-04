package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ayanot.discoveryourfantasy.entity.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

public class ContentImageFragmentForSearch extends ContentImageFragment {
    private static int offset;
    private RecyclerView recyclerView;
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
        offset = 0;
        connectionDetector = initConnectionDetector();
        recyclerView = view.findViewById(R.id.recycleView);

        initRecycleView(recyclerView);
        setLoadMoreListener(getRecycleAdapter());
    }

    private void getLoadImg() {
        int i = getPageNumber();
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(this, offset, i);
        offset += (i == 1 ? 8 : 16);
        asyncLoadImgTask.execute("/", query);
    }


    @Override
    protected void setLoadMoreListener(ImageRecycleAdapter recycleAdapter) {
        if (connectionDetector.isNetworkConnected())
            getLoadImg();
        recycleAdapter.setOnLoadMoreListener(new ImageRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!connectionDetector.isNetworkConnected())
                    Toast.makeText(getActivity(), "Please check your internet connection",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }
}
