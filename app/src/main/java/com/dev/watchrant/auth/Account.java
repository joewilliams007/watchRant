package com.dev.watchrant.auth;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Account {
    public static boolean isLoggedIn() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("logged_in", false);
    }

    public static void setLoggedIn(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged_in",b);
        editor.apply();
    }
}
