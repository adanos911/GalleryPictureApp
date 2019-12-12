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
import com.ayanot.discoveryourfantasy.helpUtil.NotificationProgressBar;
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

/**
 * <h3>Класс-активити, запускающийся первым при старте приложения.
 * Строится на основании {@link R.layout#activity_main}
 * Класс содержит в себе контейнер для фрагментов, в котором переключается фрагменты:
 * {@link ContentImageFragmentImp}
 * {@link ContentImageLasUploadedFragment}
 * {@link ProfileFragment}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class MainActivity extends AppCompatActivity {
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public static final String DISK_API_URL = "https://cloud-api.yandex.net";
    public static String TOKEN;//BuildConfig.TOKEN;
    public static String USER_NAME;//BuildConfig.USER_NAME;
    public static RestClient REST_CLIENT;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    Toolbar toolbar;
    ConnectionDetector connectionDetector;
    BottomNavigationView navigationView;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment currentFragment;

    /**
     * <p>Метод, вызывающийся при создании активити.
     *  Инициализирует соответсвующий layout и создает объекты фрагментов,
     *  для контейнера фрагментов.
     *  Устанавливает в качестве текущего фрагмента {@link ContentImageFragmentImp}</p>
     *
     * @param savedInstanceState - сохраненное состояние активити
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        connectionDetector = new ConnectionDetector(this);
        addBottomNavigationView();
        setSupportActionBar(toolbar);

        fragment1 = new ContentImageFragmentImp();
        fragment2 = new ContentImageLasUploadedFragment();
        fragment3 = new ProfileFragment();
        currentFragment = fragment1;
        if (!connectionDetector.isNetworkConnected())
            new AsyncLoadCacheTask(this).execute();
    }

    /**
     * <p>Инициализирует клиента {@link MainActivity#REST_CLIENT},
     *  для работы с Rest API yandex disk</p>
     */
    private void initClient() {
        TOKEN = getSharedPreferences(InitActivity.TOKEN_PREF, MODE_PRIVATE)
                .getString("token", "");
        USER_NAME = getSharedPreferences(InitActivity.LOGIN_PREF, MODE_PRIVATE)
                .getString("login", "");
        REST_CLIENT = RestClientFactory.getInstance(new Credentials(USER_NAME, TOKEN));
    }

    /**
     * <p>Активити находится в состоянии активного взаимодействия с пользователем</p>
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (getSharedPreferences(InitActivity.TOKEN_PREF, MODE_PRIVATE)
                .getString("token", "").equals("")) {
            Intent intent = new Intent(MainActivity.this, InitActivity.class);
            startActivityForResult(intent, 99);
        } else {
            initClient();
            checkOpenAfterNotificationClick();
        }
    }

    /**
     * <p>Устанавливает для данной активити меню, в нашем случае, это верхняя панель.
     *  Меню определено в файле {@link R.menu#menu_main}</p>
     *
     * @param menu - меню в котором размещаются элементы
     * @return - true или false(надо отображать меню или нет)
     */
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

    /**
     * <p>Обработка нажатий на элементы меню.</p>
     *
     * @param item - элемент меню который был выбран
     * @return
     */
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


    /**
     * <p>Загрузка фрагмента в котейнер фрагментов {@link R.id#frame_container},
     *  и добавление его в backStack, для возможности возврата на него</p>
     *
     * @param fragment - фрагмент на который следует переключиться
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * <p>Добавление нижней навигационной панели, для переключения между фрагментами,
     *  панель определена {@link R.id#navigationPanel}</p>
     */
    private void addBottomNavigationView() {
        navigationView = findViewById(R.id.navigationPanel);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_library:
                    if (connectionDetector.isNetworkConnected()) {
                        loadFragment(fragment1);
                        currentFragment = fragment1;
                    } else {
                        new AsyncLoadCacheTask(MainActivity.this).execute();
                    }
                    return true;
                case R.id.navigation_last:
                    loadFragment(fragment2);
                    currentFragment = fragment2;
                    return true;
                case R.id.navigation_profile:
                    loadFragment(fragment3);
                    currentFragment = fragment3;
                    return true;
            }
            return false;
        });
    }

    /**
     * <p>Метод загружающий нужный фрагмент, после выхода активити из состояния onPause
     *  При стандартном открытие активити открывается текущий фрагмент
     *  {@link MainActivity#currentFragment},
     *  Если активити была выведена из состояния pause, с помощью клика по загрузке
     *  нового изображения, то активити переключается на фрагмент
     *  {@link ContentImageLasUploadedFragment}</p>
     */
    private void checkOpenAfterNotificationClick() {
        String mes = getIntent().getStringExtra(NotificationProgressBar.OPEN_NOTIF_MES);
        if (mes != null && mes.equals("Uploading")) {
            loadFragment(fragment2);
            getIntent().removeExtra(NotificationProgressBar.OPEN_NOTIF_MES);
        } else {
            if (connectionDetector.isNetworkConnected())
                loadFragment(currentFragment);
        }
    }

    /**
     * <h3>Вложенный статический класс, для создания асинхронного потока загрузки
     *  кеша изображений, если при старте приложения нет соединения с интернетом</h3>
     */
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

    //--upload image to yandex disk-----------------------------------------------------------------

    private String currentPhotoPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File image = new File(currentPhotoPath);
            new AsyncUploadImgTask(this).execute(image);
        }
//        if (requestCode == 99 && resultCode == RESULT_OK) {}
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
                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
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