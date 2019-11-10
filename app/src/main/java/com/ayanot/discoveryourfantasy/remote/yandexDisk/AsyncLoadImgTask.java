package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.os.AsyncTask;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class AsyncLoadImgTask extends AsyncTask<String, Void, List<Image>> {
    private static final String TAG = "AsyncLoadImgTask";

    private WeakReference<OnTaskCompleted> listener;
    private int offset;
    private int pageNumber;

    public AsyncLoadImgTask(OnTaskCompleted onTaskCompleted, int offset, int pageNumber) {
        this.listener = new WeakReference<>(onTaskCompleted);
        this.offset = offset;
        this.pageNumber = pageNumber;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Image> doInBackground(String... strings) {
        int limit = (pageNumber == 1) ? 8 : 16;
        synchronized (this) {
            try {
                if (strings.length == 1)
                    return Downloader.getImages(strings[0], offset, limit);
                else if (strings[1].equals("lastUploaded"))
                    return Downloader.getLastUploadedImages(offset, limit);
                else if (strings.length == 2)
                    return Downloader.getImagesWithRegex(strings[1]);
            } catch (IOException | ServerIOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Image> images) {
        super.onPostExecute(images);
        listener.get().onTaskCompleted(images);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(List<Image> responseImage);
    }
}
