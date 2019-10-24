package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ayanot.discoveryourfantasy.dataBase.cache.DatabaseAdapter;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.entity.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

import java.util.ArrayList;
import java.util.List;

public class ContentImageFragmentImp extends ContentImageFragment {
    private static final String TAG = "ContentImageFragmentImp";

    private static int offset;
    private List<Image> cacheImages;
    private ConnectionDetector connectionDetector;
    private DatabaseAdapter databaseAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_image_fragment, container, false);
        if (getArguments() != null) {
            cacheImages = getArguments().getParcelableArrayList(ArrayList.class.getSimpleName());
        }

        setDatabaseAdapter(new DatabaseAdapter(getActivity()));
        setParameters(view);

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
            setImageList(new ArrayList<Image>());
            getImageList().addAll(cacheImages);
        }
        offset = 0;
        connectionDetector = initConnectionDetector();
        databaseAdapter = getDatabaseAdapter();
        recyclerView = view.findViewById(R.id.recycleView);

        initRecycleView(recyclerView);
        setLoadMoreListener(getRecycleAdapter());

    }

    private void getLoadImg() {
        databaseAdapter.open();
        int i = getPageNumber();
        if (i == 1 && databaseAdapter.getCount() > 8) {
            databaseAdapter.refresh();
            databaseAdapter.close();
        }
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(this, offset, i);
        offset += (i == 1 ? 8 : 16);
        asyncLoadImgTask.execute("/");
    }

    @Override
    protected void setLoadMoreListener(final ImageRecycleAdapter recycleAdapter) {
        if (connectionDetector.isNetworkConnected())
            getLoadImg();
        recycleAdapter.setOnLoadMoreListener(new ImageRecycleAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (connectionDetector.isNetworkConnected()) {
                    getImageList().add(null);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recycleAdapter.notifyItemInserted(getImageList().size() - 1);
                        }
                    });
                    int i = getPageNumber();
                    setPageNumber(++i);
                    getLoadImg();
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
