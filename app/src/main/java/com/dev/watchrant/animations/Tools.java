package com.dev.watchrant.animations;

import static com.dev.watchrant.RantActivity.toast;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.dev.watchrant.R;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        } else if (Account.theme().equals("discord")) {
            context.setTheme(R.style.Theme_Discord);
        } else if (Account.theme().equals("coffee")) {
            context.setTheme(R.style.Theme_Coffee);
        }
    }

    public static String saveToInternalStorage(String name, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(MyApplication.getAppContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

            toast("success! please reload watch face!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String name)
    {
        try {
            ContextWrapper cw = new ContextWrapper(MyApplication.getAppContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            toast("no avatar");
            return null;
        }
    }

}