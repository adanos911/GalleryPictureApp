package com.ayanot.discoveryourfantasy;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentImageFragment extends Fragment {

    RecyclerView recyclerView;
    private AssetManager assetManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_image_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        assetManager = getActivity().getApplicationContext().getAssets();
        List<String> fileNames = getListNameImages();

        final List<Image> images = new ArrayList<>();
        for (String file : fileNames) {
            images.add(new Image(file));
        }

        final ImageRecycleAdapter imageRecycleAdapter = new ImageRecycleAdapter(images);
        imageRecycleAdapter.setHasStableIds(true);
        recyclerView.setHasFixedSize(true);
        imageRecycleAdapter.setOnItemClickListener(new ImageRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Image image = imageRecycleAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("IMAGE_NAME", image.getName());
                startActivity(intent);
            }
        });
//        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(imageRecycleAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return view;
    }

    private List<String> getListNameImages() {
        String[] names;
        List<String> namesList = new ArrayList<>();
        try {
            names = assetManager.list("images");
            for (int i = 0; i < names.length; i++) {
                if (names[i].contains(".jpg"))
                    namesList.add(names[i]);
            }
            return namesList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
