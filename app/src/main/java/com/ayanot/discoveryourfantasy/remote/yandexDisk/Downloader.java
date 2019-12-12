package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

/**
 * <h3>Класс, предоставляющий методы получения данных с yandex disk,
 * с помощью {@link com.ayanot.discoveryourfantasy.MainActivity#REST_CLIENT}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class Downloader {

    /**
     * <p>Метод получает список изображений с yandex disk</p>
     *
     * @param path   - путь к ресурсу
     * @param offset - смещение относительно начала списка
     * @param limit  - количество файлов в списке
     * @return - список изображений {@link Image}
     * @throws IOException
     * @throws ServerIOException
     */
    public static List<Image> getImages(String path, int offset, Integer limit)
            throws IOException, ServerIOException {
        List<Image> images = new ArrayList<>();
        ResourceList resources;
        Resource resource;
        if (path.equals("/")) {
            resources = REST_CLIENT.getFlatResourceList(new ResourcesArgs.Builder()
                    .setLimit(limit)
                    .setMediaType("image")
                    .setOffset(offset)
                    .setSort(ResourcesArgs.Sort.name)
                    .setPreviewSize("S")
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
                    .setPreviewSize("S")
                    .build());
            for (Resource res : resource.getResourceList().getItems()) {
                if (res.getName().contains(".jpg") || res.getName().contains(".png"))
                    images.add(new Image(res.getName(), res.getPreview(),
                            "", res.getPath().getPath()));
            }
        }
        return images;
    }

    /**
     * <p>Метод получает список последних изображений, загруженных на disk</p>
     * @param offset - смещение относительно начала списка (не работает)
     * @param limit - количество файлов в списке
     * @return - список изображений {@link Image}
     * @throws IOException
     * @throws ServerIOException
     */
    public static List<Image> getLastUploadedImages(int offset, Integer limit)
            throws IOException, ServerIOException {
        List<Image> images = new ArrayList<>();
        ResourceList resources = REST_CLIENT.getLastUploadedResources(new ResourcesArgs.Builder()
                .setLimit(limit)
                .setMediaType("image")
                .setOffset(offset)
                .setPreviewSize("S")
                .build());
        for (Resource res : resources.getItems()) {
            images.add(new Image(res.getName(), res.getPreview(),
                    "", res.getPath().getPath()));
        }
        return images;
    }

    /**
     * <p>Метод получающий список изображений, название которых содержит заданную
     *  последовательность символов</p>
     * @param regex - последовательность символов для поиске
     * @return - список изображений {@link Image}
     * @throws IOException
     * @throws ServerIOException
     */
    public static List<Image> getImagesWithRegex(String regex) throws IOException, ServerIOException {
        List<Image> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        ResourceList resourceList = REST_CLIENT.getFlatResourceList(new ResourcesArgs.Builder()
                .setMediaType("image")
                .setLimit(Integer.MAX_VALUE)
                .setFields("items.name, items.preview, items.path")
                .setPreviewSize("M")
                .setPath("/")
                .build());
        for (Resource res : resourceList.getItems()) {
            if (pattern.matcher(res.getName()).matches())
                matches.add(new Image(res.getName(), res.getPreview(),
                        "", res.getPath().getPath()));
        }

        return matches;
    }

    public static Image getPreviewImage(int offset) throws IOException, ServerIOException {
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

    /**
     * <p>Метод получающий URL для превью изображения кастомного размера</p>
     * @param path - путь до изображения на disk
     * @param size - размер, может принимать значения {S, M, L, XL, XXL, XXXL},
     *             либо точное значение (например 120x120)
     * @return - URL превью изображения
     * @throws IOException
     * @throws ServerIOException
     */
    public static String getPreviewCustomSize(String path, String size)
            throws IOException, ServerIOException {
        Resource resource = REST_CLIENT.getResources(new ResourcesArgs.Builder()
                .setPath(path)
                .setFields("preview")
                .setPreviewSize(size)
                .build());
        return resource.getPreview();
    }
}
