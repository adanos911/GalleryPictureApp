package com.ayanot.discoveryourfantasy.helpUtil;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionDetector {

    Context context;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }
}
