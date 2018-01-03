package com.kreml.andre.newyorktimesrevisited.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.kreml.andre.newyorktimesrevisited.activities.NYApp;

/**
 * Created by kreml on 11/18/2017.
 */

public class InternetChangeReceiver extends BroadcastReceiver {

    public OnInternetListener mInternetListener;

    public interface OnInternetListener {
        void initiateLoading();
    }

    public void setInternetListener(OnInternetListener listener) {
        mInternetListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isInitialStickyBroadcast()) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info;
            if (connectivity!= null && (info = connectivity.getActiveNetworkInfo()) != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED || info.getState() == NetworkInfo.State.CONNECTING) {
                    if (mInternetListener != null) {
                        mInternetListener.initiateLoading();
                    }
                }
            }
        }
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) NYApp.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
