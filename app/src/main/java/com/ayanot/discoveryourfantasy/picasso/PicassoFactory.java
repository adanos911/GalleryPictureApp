package com.ayanot.discoveryourfantasy.picasso;

import android.content.Context;
import android.net.Uri;

import com.ayanot.discoveryourfantasy.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * <h3>Класс, создающий объект {@link Picasso} для работы с изображениями</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class PicassoFactory {

    public static Picasso getInstance(Context context) {
        Picasso.Builder builder = new Picasso.Builder(context);

        builder.downloader(new UrlConnectionDownloader(context) {
            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                HttpURLConnection connection = super.openConnection(uri);
                connection.setRequestProperty("Authorization", "OAuth " + MainActivity.TOKEN);
                return connection;
            }
        });
        return builder.build();
    }
}
