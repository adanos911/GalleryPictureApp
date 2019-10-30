package com.ayanot.discoveryourfantasy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.picasso.PicassoFactory;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    ImageView imageView;
    TextView descriptionImage;
    Toolbar toolbar;
    Image image;
    Picasso picasso;
    LinearLayout linLaDownloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        picasso = PicassoFactory.getInstance(this);
        imageView = findViewById(R.id.fullScreenImage);
        descriptionImage = findViewById(R.id.descriptionImage);
        toolbar = findViewById(R.id.onImageToolbar);
        linLaDownloadProgress = findViewById(R.id.linLaDownloadProgress);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image = getIntent().getExtras().getParcelable(Image.class.getSimpleName());

        new AsyncDownloadImageTask(linLaDownloadProgress, imageView, picasso).execute(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_one_image, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.saveButton) {
                View itemChoose = item.getActionView();
                if (itemChoose != null) {
                    itemChoose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AsyncDownloadToStoreTask(ImageActivity.this).execute(image);
                        }
                    });
                }
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    static class AsyncDownloadToStoreTask extends AsyncTask<Image, Void, Void> {
        Bitmap bitmap;
        private final WeakReference<ImageActivity> imageActivityWeakReference;

        AsyncDownloadToStoreTask(ImageActivity activity) {
            this.imageActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Image... images) {
            try {
                Image image = images[0];
                image.setHref(REST_CLIENT.getDownloadLink(image.getPath()).getHref());
                URL url = new URL(image.getHref());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                MediaStore.Images.Media.insertImage(imageActivityWeakReference.get().getContentResolver(),
                        bitmap, image.getName(), "");
            } catch (IOException | ServerIOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(imageActivityWeakReference.get(), "Load success",
                    Toast.LENGTH_SHORT).show();
        }
    }

    static class AsyncDownloadImageTask extends AsyncTask<Image, Void, String> {
        private final WeakReference<LinearLayout> linLaDownloadProgress;
        private final WeakReference<ImageView> imageViewWeakReference;
        private final Picasso picasso;

        AsyncDownloadImageTask(LinearLayout linearLayout, ImageView imageView, Picasso picasso) {
            this.linLaDownloadProgress = new WeakReference<>(linearLayout);
            this.imageViewWeakReference = new WeakReference<>(imageView);
            this.picasso = picasso;
        }

        @Override
        protected void onPreExecute() {
            linLaDownloadProgress.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Image... images) {
            try {
                return Downloader.getPreviewCustomSize(images[0].getPath(), "XL");
            } catch (IOException | ServerIOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String previewUrl) {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    linLaDownloadProgress.get().setVisibility(View.GONE);
                    imageViewWeakReference.get().setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(TAG, errorDrawable.toString());
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            imageViewWeakReference.get().setTag(target);
            picasso.load(previewUrl).into(target);
        }
    }
}