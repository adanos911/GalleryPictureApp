package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.app.Activity;
import android.os.AsyncTask;

import com.ayanot.discoveryourfantasy.helpUtil.NotificationProgressBar;
import com.yandex.disk.rest.exceptions.ServerException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class AsyncUploadImgTask extends AsyncTask<File, Void, String> {
    private final WeakReference<Activity> activityWeakReference;
    private NotificationProgressBar notificationProgressBar;

    public AsyncUploadImgTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        notificationProgressBar = new NotificationProgressBar(activityWeakReference.get(), "Uploading", 2);
    }

    @Override
    protected void onPreExecute() {
        notificationProgressBar.show();
    }

    @Override
    protected String doInBackground(File... files) {
        try {
            Uploader.uploadFile("/" + files[0].getName(), files[0]);
            return files[0].getName();
        } catch (IOException | ServerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String fileName) {
        notificationProgressBar.end("File " + fileName + " uploaded", "Upload success");
//        Toast.makeText(activityWeakReference.get(), "Upload success", Toast.LENGTH_SHORT)
//                .show();
    }
}
