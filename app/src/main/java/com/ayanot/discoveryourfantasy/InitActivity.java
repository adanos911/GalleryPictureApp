package com.ayanot.discoveryourfantasy;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ayanot.discoveryourfantasy.MainActivity.CLIENT_ID;

/**
 * <h3>Класс-активити, строится на основании {@link R.layout#activity_init}
 * Отвечает за аутентификацию пользователя и получения токена, для
 * доступа к его yandex disk</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class InitActivity extends AppCompatActivity {
    public static final String AUTH_URL =
            "https://oauth.yandex.ru/authorize?response_type=token&client_id=" + CLIENT_ID;
    static final String TOKEN_PREF = "TOKEN_PREF";
    static final String LOGIN_PREF = "LOGIN_PREF";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        WebView webViewToken = findViewById(R.id.initToken);
        webViewToken.getSettings().setJavaScriptEnabled(true);
        webViewToken.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("access_token")) {
                    Pattern tokenPattern = Pattern.compile("#access_token.+?&");
                    Matcher tokenMatcher = tokenPattern.matcher(url);
                    if (tokenMatcher.find()) {
                        String partWithToken = tokenMatcher.group();
                        SharedPreferences tokenPref = getSharedPreferences(TOKEN_PREF, MODE_PRIVATE);
                        tokenPref.edit()
                                .putString("token", partWithToken.substring(14, partWithToken.length() - 1))
                                .apply();
                        view.evaluateJavascript(
                                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                                html -> {
                                    Pattern loginPattern = Pattern.compile("login.+?\\\\.+?\\\\");
                                    Matcher loginMatcher = loginPattern.matcher(html);
                                    if (loginMatcher.find()) {
                                        String partWithLogin = loginMatcher.group();
                                        SharedPreferences loginPref = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
                                        loginPref.edit()
                                                .putString("login", partWithLogin.substring(10, partWithLogin.length() - 1) +
                                                        "@yandex.ru")
                                                .apply();
                                    }
                                });
                        finish();
                    }
//                    setResult(RESULT_OK);
                }
            }
        });
        webViewToken.loadUrl(AUTH_URL);
    }
}
