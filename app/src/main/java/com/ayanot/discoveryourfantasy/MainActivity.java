package com.ayanot.discoveryourfantasy;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.ayanot.discoveryourfantasy.dataBase.cache.ImageDatabase;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.helpUtil.SearchSuggestionProvider;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.AsyncUploadImgTask;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Credentials;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClient;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClientFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public static final String USER_NAME = BuildConfig.USER_NAME;
    public static final String AUTH_URL =
            "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    public static final String TOKEN = BuildConfig.TOKEN;
    public static final RestClient REST_CLIENT =
            RestClientFactory.getInstance(new Credentials(MainActivity.USER_NAME, MainActivity.TOKEN));
    public static final String DISK_API_URL = "https://cloud-api.yandex.net";
    private static final String TAG = "MainActivity";

    Toolbar toolbar;
    ConnectionDetector connectionDetector;
    //--upload image to yandex disk-----------------------------------------------------------------
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    BottomNavigationView navigationView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;
    }

    //Authorization: OAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.mainToolbar);
        connectionDetector = new ConnectionDetector(this);
        addBottomNavigationView();

        if (connectionDetector.isNetworkConnected())
            loadFragment(new ContentImageFragmentImp());
        else {
            new AsyncLoadCacheTask(this).execute();
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_history) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.clearHistory();
        }
        if (item.getItemId() == R.id.take_photo)
            dispatchTakePictureIntent();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Fragment contentImg =
                getSupportFragmentManager().findFragmentById(R.id.contentImageFragment);
//        if (contentImg != null) {
//            outState.putParcelable();
//        }
        super.onSaveInstanceState(outState);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void addBottomNavigationView() {
        navigationView = findViewById(R.id.navigationPanel);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_library:
                        loadFragment(new ContentImageFragmentImp());
                        return true;
                    case R.id.navigation_last:
                        loadFragment(new ContentImageFragmentLasUploaded());
                        return true;
                    case R.id.navigation_profile:
                        loadFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        });
    }

    private static class AsyncLoadCacheTask extends AsyncTask<Void, Void, List<Image>> {
        private final WeakReference<MainActivity> mainActivityWeakReference;

        AsyncLoadCacheTask(MainActivity activity) {
            this.mainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Image> doInBackground(Void... voids) {
            return ImageDatabase.getInstance(mainActivityWeakReference.get()).imageDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Image> imageList) {
            Bundle bundleCacheImg = new Bundle();
            bundleCacheImg.putParcelableArrayList(ArrayList.class.getSimpleName(),
                    (ArrayList<? extends Parcelable>) imageList);
            ContentImageFragmentImp contentImageFragmentImp = new ContentImageFragmentImp();
            contentImageFragmentImp.setArguments(bundleCacheImg);
            mainActivityWeakReference.get().loadFragment(contentImageFragmentImp);
            super.onPostExecute(imageList);
        }

    }
    private String currentPhotoPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File image = new File(currentPhotoPath);
            new AsyncUploadImgTask(this).execute(image);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ayanot.discoveryourfantasy.fileprovider",
                        photoFile);
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}