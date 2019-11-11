package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ayanot.discoveryourfantasy.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.dataBase.cache.ImageDatabase;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

public class ContentImageLasUploadedFragment extends ContentImageFragmentImp {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_image_fragment, container, false);

        setImageDatabase(ImageDatabase.getInstance(getContext()));
        setParameters(view);

        return view;
    }

    private void setParameters(View view) {
        recyclerView = view.findViewById(R.id.recycleView);

        initRecycleView(recyclerView);
        setLoadMoreListener(getRecycleAdapter());
        setRefreshLayout(view);
    }

    private void getLoadImg() {
        int i = getPageNumber();
        int offset = getOffset();
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(this, offset, i);
        setOffset(offset + (i == 1 ? 8 : 16));
        asyncLoadImgTask.execute("/", "lastUploaded");
    }

    @Override
    protected void setLoadMoreListener(final ImageRecycleAdapter recycleAdapter) {
        if (isNetworkConnection())
            getLoadImg();
        recycleAdapter.setOnLoadMoreListener(() -> {
            if (isNetworkConnection()) {
                getImageList().add(null);
                recyclerView.post(() -> recycleAdapter.notifyItemInserted(getImageList().size() - 1));
                int i = getPageNumber();
                setPageNumber(++i);
                getLoadImg();
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
