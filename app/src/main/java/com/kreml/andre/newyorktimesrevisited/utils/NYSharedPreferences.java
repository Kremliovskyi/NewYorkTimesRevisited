package com.kreml.andre.newyorktimesrevisited.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class NYSharedPreferences {

    private SharedPreferences mGeneralPreferences;

    private NYSharedPreferences(Context context) {
        this.mGeneralPreferences = context.getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    private static NYSharedPreferences sInstance;

    public static NYSharedPreferences getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NYSharedPreferences(context);
        }
        return sInstance;
    }


    private SharedPreferences getSharedPreferences() {
        return mGeneralPreferences;
    }

    public void setUserLoggedIn(boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.IS_USER_LOGGED_IN, newValue);
        editor.apply();
    }

    public boolean getUserLoggedIn() {
        return getSharedPreferences().getBoolean(Constants.IS_USER_LOGGED_IN, false);
    }

    public String getUsername() {
        return getSharedPreferences().getString(Constants.USERNAME, "");
    }

    public void setUserName(String name) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Constants.USERNAME, name);
        editor.apply();
    }

    public void setCategoryPreference(String category, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(category, value);
        editor.apply();
    }

    public boolean getCategoryPreference(String category) {
        return getSharedPreferences().getBoolean(category, true);
    }

    public void clearPreferences() {
        getSharedPreferences().edit().clear().apply();
    }
}