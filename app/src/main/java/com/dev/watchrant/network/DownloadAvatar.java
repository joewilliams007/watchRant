package com.dev.watchrant.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dev.watchrant.animations.Tools;

import java.io.InputStream;

public class DownloadAvatar extends AsyncTask<String, Void, Bitmap> {

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("ErrorL", e.getMessage());
            e.printStackTrace();
         //   mIcon11 = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),R.drawable.error);
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        Tools.saveToInternalStorage(result);
    }
}
