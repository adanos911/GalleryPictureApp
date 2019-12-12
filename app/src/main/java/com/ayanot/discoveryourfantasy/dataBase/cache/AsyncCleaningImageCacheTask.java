package com.ayanot.discoveryourfantasy.dataBase.cache;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * <h3>Класс, создающий асинхронный процесс,
 * очищающий кеш изображений в БД</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class AsyncCleaningImageCacheTask extends AsyncTask<Void, Void, Integer> {

    private final WeakReference<Context> referenceContext;

    public AsyncCleaningImageCacheTask(Context context) {
        this.referenceContext = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ImageDao imageDao = ImageDatabase.getInstance(referenceContext.get()).imageDao();
        return imageDao.getRowCount();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (integer >= 8) {
            new AsyncClearCacheTask(referenceContext.get()).execute();
        }
        super.onPostExecute(integer);
    }

    private static class AsyncClearCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> contextWeakReference;

        AsyncClearCacheTask(Context context) {
            this.contextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ImageDatabase.getInstance(contextWeakReference.get()).imageDao().deleteOldRows();
            return null;
        }
    }
}
