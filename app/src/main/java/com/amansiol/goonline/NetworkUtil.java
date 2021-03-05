package com.amansiol.goonline;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

class NetworkUtil {
    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null) {
                if (nInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    connected = true;
                    return connected;
                } else if (nInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    connected = true;
                    return connected;
                }
            } else {
                connected =false;
                return connected;
            }
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public static void checkConnection(Context context) {
        if (isConnected(context)) {
            context.startActivity(new Intent(context, NoInternet.class));
        }
    }
}

