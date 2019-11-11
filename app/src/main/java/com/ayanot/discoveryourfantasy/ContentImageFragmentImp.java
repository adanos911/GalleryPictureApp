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
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

import java.util.ArrayList;
import java.util.List;

public class ContentImageFragmentImp extends ContentImageFragment {
    private static final String TAG = "ContentImageFragmentImp";

    private List<Image> cacheImages;
    private RecyclerView recyclerView;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            cacheImages = getArguments().getParcelableArrayList(ArrayList.class.getSimpleName());
        }
        if (view == null) {
            view = inflater.inflate(R.layout.content_image_fragment, container, false);
            setImageDatabase(ImageDatabase.getInstance(getContext()));
            setParameters(view);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (getArguments() != null)
//            getArguments().remove(ArrayList.class.getSimpleName());
    }

    private void setParameters(View view) {
        if (cacheImages != null) {
            getImageList().addAll(cacheImages);
        }
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
        asyncLoadImgTask.execute("/");
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
                Toast.makeText(getActivity(), getResources().getString
                        (R.string.toast_network_connection_text), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
