package com.ayanot.discoveryourfantasy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.ServerIOException;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    ImageView imageView;
    TextView descriptionImage;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.fullScreenImage);
        descriptionImage = findViewById(R.id.descriptionImage);

        Image image = getIntent().getExtras().getParcelable(Image.class.getSimpleName());

        new AsyncDownloadImage().execute(image);

//        descriptionImage.setText(image.getName().split("\\.")[0]);
    }

    class AsyncDownloadImage extends AsyncTask<Image, Void, Image> {
        LinearLayout linlaDownloadProgress = findViewById(R.id.linlaDownloadProgress);

        @Override
        protected Image doInBackground(Image... images) {
            try {
                images[0].setHref(REST_CLIENT.getDownloadLink(images[0].getPath()).getHref());
            } catch (NetworkIOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (ServerIOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return images[0];
        }

        @Override
        protected void onPostExecute(Image image) {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    linlaDownloadProgress.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(TAG, errorDrawable.toString());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    linlaDownloadProgress.setVisibility(View.VISIBLE);
                }
            };
            imageView.setTag(target);
            Picasso.with(context).load(image.getHref())
                    .into(target);
        }
    }
}
