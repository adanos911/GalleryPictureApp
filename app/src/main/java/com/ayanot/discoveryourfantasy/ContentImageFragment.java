package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.entity.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Credentials;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClientFactory;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;

import java.util.ArrayList;
import java.util.List;

public class ContentImageFragment extends Fragment {
    private static final String TAG = "ContentImageFragment";

    RecyclerView recyclerView;
    private RestClient restClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_image_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycleView);

        restClient = RestClientFactory.getInstance(new Credentials(MainActivity.USER_NAME, MainActivity.TOKEN));
        try {
            List<Image> images = new AsyncRequestHref().execute("/Израиль").get();

            final ImageRecycleAdapter imageRecycleAdapter = new ImageRecycleAdapter(images);
            recyclerView.setHasFixedSize(true);
            imageRecycleAdapter.setOnItemClickListener(new ImageRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    Image image = imageRecycleAdapter.getItem(position);
                    new AsyncDownloadImage().execute(image);
                    //FIXME: DELETE THREAD SLEEP!!!
                    SystemClock.sleep(1000);
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra(Image.class.getSimpleName(), image);
                    startActivity(intent);
                }
            });
//        recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(imageRecycleAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    private List<Image> getListImageFromYandexDisk(RestClient restClient, String path) {
        Resource resources;
        List<Image> images = new ArrayList<>();
        try {
            resources = restClient.getResources(new ResourcesArgs.Builder()
                    .setPath(path)
                    .setLimit(Integer.MAX_VALUE)
                    .setPreviewSize("L")
                    .build());
            List<Resource> items = resources.getResourceList().getItems();
            for (Resource item : items) {
                String itemName = item.getName();
                if (itemName.contains(".jpg")) {
                    images.add(new Image(itemName, item.getPreview(), "",
                            path + "/" + itemName));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return images;
    }

    class AsyncRequestHref extends AsyncTask<String, Void, List<Image>> {
        @Override
        protected List<Image> doInBackground(String... paths) {
            return getListImageFromYandexDisk(restClient, paths[0]);
        }
    }

    class AsyncDownloadImage extends AsyncTask<Image, Void, Image> {
        @Override
        protected Image doInBackground(Image... images) {
            try {
                images[0].setHref(restClient.getDownloadLink(images[0].getPath()).getHref());
            } catch (NetworkIOException e) {
                e.printStackTrace();
            } catch (ServerIOException e) {
                e.printStackTrace();
            }
            return images[0];
        }
    }

}
