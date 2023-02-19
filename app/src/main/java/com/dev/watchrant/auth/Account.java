package com.dev.watchrant.auth;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dev.watchrant.adapters.OptionsItem;

public class Account {
    public static String key() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("key", null);
    }

    public static void setKey(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("key",key);
        editor.apply();
    }

    public static int id() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("id", 0);
    }

    public static void setId(int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("id",id);
        editor.apply();
    }

    public static int user_id() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("user_id", 0);
    }

    public static void setUser_id(int user_id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("user_id",user_id);
        editor.apply();
    }

    public static int expire_time() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("expire_time", 0);
    }

    public static void setExpire_time(int expire_time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("expire_time",expire_time);
        editor.apply();
    }

    public static Boolean isLoggedIn() {
        return Account.user_id() != 0;
    }

    public static String theme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("theme", "dark");
    }

    public static void setTheme(String theme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("theme",theme);
        editor.apply();
    }

    public static Boolean animate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("animate", true);
    }

    public static void setAnimate(Boolean animate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("animate",animate);
        editor.apply();
    }

    public static Boolean vibrate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("vibrate", true);
    }

    public static void setVibrate(Boolean vibrate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("vibrate",vibrate);
        editor.apply();
    }

    public static int limit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("limit", 50);
    }

    public static void setLimit(int limit) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("limit",limit);
        editor.apply();
    }
}
