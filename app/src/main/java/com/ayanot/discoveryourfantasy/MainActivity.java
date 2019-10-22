package com.ayanot.discoveryourfantasy;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.ayanot.discoveryourfantasy.dataBase.DatabaseAdapter;
import com.ayanot.discoveryourfantasy.entity.Image;
import com.ayanot.discoveryourfantasy.helpUtil.ConnectionDetector;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.Credentials;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClient;
import com.ayanot.discoveryourfantasy.remote.yandexDisk.RestClientFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;
    public static final String USER_NAME = BuildConfig.USER_NAME;
    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    public static final String TOKEN = BuildConfig.TOKEN;
    public static final RestClient REST_CLIENT =
            RestClientFactory.getInstance(new Credentials(MainActivity.USER_NAME, MainActivity.TOKEN));
    public static final String DISK_API_URL = "https://cloud-api.yandex.net";
    private static final String TAG = "MainActivity";

    Toolbar toolbar;
    Button refreshButton;
    ConnectionDetector connectionDetector;

    //Authorization: OAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.mainToolbar);
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setVisibility(View.GONE);
        connectionDetector = new ConnectionDetector(this);
        Log.d("ALOHA", "START!!!");

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isNetworkConnected()) {
                    refreshButton.setVisibility(View.GONE);
                    addFragment(new ContentImageFragment());
                } else {
                    Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        if (connectionDetector.isNetworkConnected())
            addFragment(new ContentImageFragment());
        else {
//            refreshButton.setVisibility(View.VISIBLE);
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
            databaseAdapter.open();
            List<Image> images = databaseAdapter.getImages();
            databaseAdapter.close();
            Bundle bundleCacheImg = new Bundle();
            bundleCacheImg.putParcelableArrayList(Image.class.getSimpleName(), (ArrayList<? extends Parcelable>) images);
//            for (int i = 0; i < images.size(); i++)
//                bundleCacheImg.putParcelable(String.valueOf(i), images.get(i));
            ContentImageFragment contentImageFragment = new ContentImageFragment();
            contentImageFragment.setArguments(bundleCacheImg);
            addFragment(contentImageFragment);
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity, fragment).commit();
    }
}