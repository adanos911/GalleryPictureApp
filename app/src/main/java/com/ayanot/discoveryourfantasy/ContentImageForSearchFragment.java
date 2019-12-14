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
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncLoadImgTask;

/**
 * <h3>Класс-фрагмент, реализующий {@link ContentImageFragment}
 * Предназначен для отображения изображений найденных в результате поиска</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class ContentImageForSearchFragment extends ContentImageFragment {

    private RecyclerView recyclerView;
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
        recyclerView = view.findViewById(R.id.recycleView);

        initRecycleView(recyclerView);
        setLoadMoreListener(getRecycleAdapter());
        setRefreshLayout(view);
        getSwipeRefreshLayout().setRefreshing(false);
        getSwipeRefreshLayout().setEnabled(false);
    }

    /**
     * <p>Метод, загружает порцию изображений с yandex disk, начиная с текущего
     * значения offset {@link ContentImageFragment#getOffset()},
     * и вставляет их в recycleView</p>
     */
    private void getLoadImg() {
        int i = getPageNumber();
        int offset = getOffset();
        AsyncLoadImgTask asyncLoadImgTask = new AsyncLoadImgTask(this, offset, i);
        setOffset(offset + (i == 1 ? 8 : 16));
        asyncLoadImgTask.execute("/", query);
    }


    @Override
    protected void setLoadMoreListener(ImageRecycleAdapter recycleAdapter) {
        if (isNetworkConnection())
            getLoadImg();
        recycleAdapter.setOnLoadMoreListener(() -> {
            if (!isNetworkConnection())
                Toast.makeText(getActivity(), getResources().getString(
                        R.string.toast_network_connection_text), Toast.LENGTH_SHORT).show();
        });
    }
}
