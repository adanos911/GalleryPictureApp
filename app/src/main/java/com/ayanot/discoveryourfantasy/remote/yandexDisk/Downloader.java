package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class Downloader {

    public static List<Image> getImages(String path, int offset, Integer limit) throws IOException, ServerIOException {
        List<Image> images = new ArrayList<>();
        ResourceList resources;
        Resource resource;
        if (path.equals("/")) {
            resources = REST_CLIENT.getFlatResourceList(new ResourcesArgs.Builder()
                    .setLimit(limit)
                    .setMediaType("image")
                    .setOffset(offset)
                    .setSort(ResourcesArgs.Sort.name)
                    .setPreviewSize("M")
                    .build());
            for (Resource res : resources.getItems()) {
                images.add(new Image(res.getName(), res.getPreview(),
                        "", res.getPath().getPath()));
            }
        } else {
            resource = REST_CLIENT.getResources(new ResourcesArgs.Builder()
                    .setLimit(limit)
                    .setPath(path)
                    .setOffset(offset)
                    .setSort(ResourcesArgs.Sort.name)
                    .setPreviewSize("M")
                    .build());
            for (Resource res : resource.getResourceList().getItems()) {
                if (res.getName().contains(".jpg") || res.getName().contains(".png"))
                    images.add(new Image(res.getName(), res.getPreview(),
                            "", res.getPath().getPath()));
            }
        }
        return images;
    }

    public static Image getPreviewImage(String path, int offset) throws IOException, ServerIOException {
        ResourceList resourceList = REST_CLIENT.getFlatResourceList(new ResourcesArgs.Builder()
                .setLimit(1)
                .setMediaType("image")
                .setOffset(offset)
                .setSort(ResourcesArgs.Sort.name)
                .setPreviewSize("M")
                .build());
        Resource resource = resourceList.getItems().get(0);
        return new Image(resource.getName(), resource.getPreview(), "", resource.getPath().getPath());
    }
}
