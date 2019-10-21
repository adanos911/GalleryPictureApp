package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.util.List;

public class AsyncLoadImgTask extends AsyncTask<Object, Void, List<Image>> {
    private static final String TAG = "AsyncLoadImgTask";

    private OnTaskCompleted listener;
    private Context context;
    private int offset;
    private boolean first;

    public AsyncLoadImgTask(Context context, OnTaskCompleted onTaskCompleted, int offset, boolean first) {
        this.context = context;
        this.listener = onTaskCompleted;
        this.offset = offset;
        this.first = first;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        while (!isNetworkConnected()) {
//            Toast.makeText(context, "Please check your Internet Connection", Toast.LENGTH_LONG)
//                    .show();
//            SystemClock.sleep(1000);
//        }
    }

    @Override
    protected List<Image> doInBackground(Object... objects) {
        int limit = first ? 8 : 16;
        synchronized (this) {
            try {
                return Downloader.getImages("/", offset, limit);
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

    private boolean isNetworkConnected() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }

}
