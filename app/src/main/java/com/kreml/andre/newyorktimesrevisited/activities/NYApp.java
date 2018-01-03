package com.kreml.andre.newyorktimesrevisited.activities;

import android.app.Application;

import io.realm.Realm;

public class NYApp extends Application {

    private static NYApp sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        sApp = this;
    }

    public static NYApp getApp() {
        return sApp;
    }
}
