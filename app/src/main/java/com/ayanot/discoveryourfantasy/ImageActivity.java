package com.ayanot.discoveryourfantasy;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.picasso.PicassoFactory;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yandex.disk.rest.exceptions.ServerIOException;

import java.io.IOException;
import java.net.URL;

import static com.ayanot.discoveryourfantasy.MainActivity.REST_CLIENT;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    ImageView imageView;
    TextView descriptionImage;
    Toolbar toolbar;
    Image image;
    final Context context = this;
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        picasso = PicassoFactory.getInstance(this);
        imageView = findViewById(R.id.fullScreenImage);
        descriptionImage = findViewById(R.id.descriptionImage);
        toolbar = findViewById(R.id.onImageToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image = getIntent().getExtras().getParcelable(Image.class.getSimpleName());

        new AsyncDownloadImage().execute(image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_one_image, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.downloadButton) {
            new DownloadToStoreTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadToStoreTask extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
//                imageView.invalidate();
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                bitmap = drawable.getBitmap();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                image.setHref(REST_CLIENT.getDownloadLink(image.getPath()).getHref());
                URL url = new URL(image.getHref());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, image.getName(), "");
            } catch (IOException | ServerIOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context, "Load success", Toast.LENGTH_SHORT).show();
        }
    }

    class AsyncDownloadImage extends AsyncTask<Image, Void, String> {
        LinearLayout linlaDownloadProgress = findViewById(R.id.linlaDownloadProgress);

        @Override
        protected String doInBackground(Image... images) {
            try {
                return Downloader.getPreviewCustomSize(image.getPath(), "XL");
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
            picasso.load(previewUrl).into(target);
        }
    }
}