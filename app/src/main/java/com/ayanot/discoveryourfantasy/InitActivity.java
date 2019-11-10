package com.ayanot.discoveryourfantasy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ayanot.discoveryourfantasy.MainActivity.CLIENT_ID;

public class InitActivity extends AppCompatActivity {
    public static final String AUTH_URL =
            "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    static final String TOKEN_PREF = "TOKEN_PREF";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        WebView token = findViewById(R.id.initToken);
        token.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("access_token")) {
                    Pattern pattern = Pattern.compile("#access_token.+?&");
                    Matcher matcher = pattern.matcher(url);
                    if (matcher.find()) {
                        String partWithToken = matcher.group();
                        String token = partWithToken.substring(14, partWithToken.length() - 1);
                        SharedPreferences tokenPref = getSharedPreferences(TOKEN_PREF, MODE_PRIVATE);
                        tokenPref.edit().putString("token", token).commit();
                        finish();
                    }
//                    setResult(RESULT_OK);
                }
            }
        });
        token.loadUrl(AUTH_URL);
    }
}
