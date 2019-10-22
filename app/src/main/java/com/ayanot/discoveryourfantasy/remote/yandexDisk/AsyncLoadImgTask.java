package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.os.AsyncTask;
import android.util.Log;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.util.List;

public class AsyncLoadImgTask extends AsyncTask<String, Void, List<Image>> {
    private static final String TAG = "AsyncLoadImgTask";

    private OnTaskCompleted listener;
    private int offset;
    private boolean first;

    public AsyncLoadImgTask(OnTaskCompleted onTaskCompleted, int offset, boolean first) {
        this.listener = onTaskCompleted;
        this.offset = offset;
        this.first = first;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Image> doInBackground(String... strings) {
        int limit = first ? 8 : 16;
        synchronized (this) {
            try {
                return Downloader.getImages(strings[0], offset, limit);
            } catch (IOException | ServerIOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(List<Image> images) {
        super.onPostExecute(images);
        listener.onTaskCompleted(images);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(List<Image> responseImage);
    }
}
