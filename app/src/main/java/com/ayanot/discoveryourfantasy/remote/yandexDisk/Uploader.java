package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Link;

import java.io.File;
import java.io.IOException;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class Uploader {

    static void uploadFile(String path, File file) throws IOException, ServerException {
        Link link = REST_CLIENT.getUploadLink(path, true);
        REST_CLIENT.uploadFile(link, false, file, new ProgressListener() {
            @Override
            public void updateProgress(long loaded, long total) {

            }

            @Override
            public boolean hasCancelled() {
                return false;
            }
        });
    }
}
