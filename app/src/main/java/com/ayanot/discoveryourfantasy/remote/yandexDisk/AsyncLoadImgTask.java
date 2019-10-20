package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.app.ProgressDialog;
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
    private ProgressDialog progressDialog;
    private int offset;

    public AsyncLoadImgTask(Context context, OnTaskCompleted onTaskCompleted, int offset) {
        this.context = context;
        this.listener = onTaskCompleted;
        this.offset = offset;
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
        synchronized (this) {
            try {
                return Downloader.getImages("/", offset, 8);
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
