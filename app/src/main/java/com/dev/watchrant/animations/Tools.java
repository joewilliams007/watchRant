package com.dev.watchrant.animations;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.dev.watchrant.R;
import com.dev.watchrant.auth.Account;

public class Tools {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



    private static int screenWidth = 0, screenHeight = 0;
    public static int getScreenWidth(Context context){
        if (screenWidth != 0)
            return screenWidth;

        calculateScreenSize(context);
        return screenWidth;
    }
    public static int getScreenHeight(Context context) {
        if (screenHeight != 0)
            return screenHeight;

        calculateScreenSize(context);
        return screenHeight;
    }

    private static void calculateScreenSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static void setTheme(Context context) {
        if (Account.theme().equals("dark")) {
            context.setTheme(R.style.Theme_Dark);
        } else if (Account.theme().equals("amoled")) {
            context.setTheme(R.style.Theme_Amoled);
        } else if (Account.theme().equals("amoled_part")) {
            context.setTheme(R.style.Theme_AmoledPart);
        } else if (Account.theme().equals("green")) {
            context.setTheme(R.style.Theme_Green);
        }
    }
}