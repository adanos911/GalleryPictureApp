package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayanot.discoveryourfantasy.remote.yandexDisk.Credentials;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClient;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClientFactory;

public class MainActivity extends AppCompatActivity {
    public static final RestClient REST_CLIENT =
            RestClientFactory.getInstance(new Credentials(MainActivity.USER_NAME, MainActivity.TOKEN));

    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public static final String USER_NAME = BuildConfig.USER_NAME;
    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    public static final String TOKEN = BuildConfig.TOKEN;
    public static final String DISK_API_URL = "https://cloud-api.yandex.net";
    private static final String TAG = "MainActivity";


    //Authorization: OAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}