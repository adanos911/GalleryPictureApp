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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.entity.adapter.ImageRecycleAdapter;
import com.ayanot.discoveryourfantasy.entity.adapter.SpacesItemDecoration;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.json.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class ContentImageFragment extends Fragment {
    private static final String TAG = "ContentImageFragment";

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_image_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        try {
            new AsyncRequestHref().execute("/Израиль").get();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        return view;
    }

    class AsyncRequestHref extends AsyncTask<String, Image, List<Image>> {
        ImageRecycleAdapter imageRecycleAdapter;
        List<Image> imagesList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            imageRecycleAdapter = new ImageRecycleAdapter(imagesList);
            recyclerView.setHasFixedSize(true);
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
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.addItemDecoration(new SpacesItemDecoration(8, 16));
        }

        @Override
        protected List<Image> doInBackground(String... paths) {
            Resource resources;
            try {
                long start = SystemClock.currentThreadTimeMillis();

                resources = REST_CLIENT.getResources(new ResourcesArgs.Builder()
                        .setPath(paths[0])
                        .setLimit(Integer.MAX_VALUE)
                        .setPreviewSize("M")
                        .build());
                long end1 = SystemClock.currentThreadTimeMillis();
                Log.d(TAG, "TIME FOR FORMMED RES = " + (end1 - start));
                List<Resource> items = resources.getResourceList().getItems();
                long end = SystemClock.currentThreadTimeMillis();
                Log.d(TAG, "TIMEEEEEEEEE GET RESOURCES LIST = " + (end - start));
                for (int i = 0; i < items.size(); i++) {
                    String itemName = items.get(i).getName();
                    long end2 = SystemClock.currentThreadTimeMillis();
                    Log.d(TAG, "TIME 2 = " + (end2 - start));
                    if (itemName.contains(".jpg")) {
                        imagesList.add(new Image(itemName, items.get(i).getPreview(), "",
                                paths[0] + "/" + itemName));
                        publishProgress(imagesList.get(i));
                        Log.d(TAG, "PUBLISH WITH = " + i);
                    }
                    long end3 = SystemClock.currentThreadTimeMillis();
                    Log.d(TAG, "TIME 3 = " + (end3 - start));
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return imagesList;
        }

        @Override
        protected void onProgressUpdate(Image... images) {
            imageRecycleAdapter.notifyDataSetChanged();
            Log.d(TAG, "DOWNLOAD IMAGE WITH LIST SIZE = " + imagesList.size());
        }
    }

}
