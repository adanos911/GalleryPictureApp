package com.ayanot.discoveryourfantasy;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.ayanot.discoveryourfantasy.dataBase.cache.ImageDatabase;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.helpUtil.SearchSuggestionProvider;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Credentials;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClient;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClientFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

    //Authorization: OAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.mainToolbar);
        connectionDetector = new ConnectionDetector(this);

        if (connectionDetector.isNetworkConnected())
            addFragment(new ContentImageFragmentImp());
        else {
            new AsyncLoadCacheTask(this).execute();
        }
        setSupportActionBar(toolbar);
    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear_history) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.clearHistory();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity, fragment).commit();
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
            mainActivityWeakReference.get().addFragment(contentImageFragmentImp);
            super.onPostExecute(imageList);
        }

    }
}