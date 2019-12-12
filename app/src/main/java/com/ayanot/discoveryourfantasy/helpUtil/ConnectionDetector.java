package com.ayanot.discoveryourfantasy.helpUtil;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * <h3>Класс, предназначенный для проверки соединения приложения с интернетом</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }
}
