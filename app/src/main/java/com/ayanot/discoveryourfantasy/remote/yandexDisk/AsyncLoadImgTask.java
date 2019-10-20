package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.util.List;

public class AsyncLoadImgTask extends AsyncTask<Object, Void, List<Image>> {

    public OnTaskCompleted listener;
    Context context;
    int pageNumber;
    boolean first;
    private ProgressDialog progressDialog;
    private int offset;

    public AsyncLoadImgTask(Context context, OnTaskCompleted onTaskCompleted, int pageNumber, int offset, boolean first) {
        this.context = context;
        this.listener = onTaskCompleted;
        this.pageNumber = pageNumber;
        this.offset = offset;
        this.first = first;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Image> doInBackground(Object... objects) {
        int limit;
        limit = first ? 6 : Integer.MAX_VALUE;
        synchronized (this) {
            try {
                return Downloader.getImages("/", offset, limit);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ServerIOException e) {
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
