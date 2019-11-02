package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.yandex.disk.rest.exceptions.ServerException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class AsyncUploadImgTask extends AsyncTask<File, Void, Void> {
    private final WeakReference<Activity> activityWeakReference;

    public AsyncUploadImgTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(File... files) {
        try {
            Uploader.uploadFile("/ala/" + files[0].getName(), files[0]);
        } catch (IOException | ServerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(activityWeakReference.get(), "Upload success", Toast.LENGTH_SHORT)
                .show();
    }
}
