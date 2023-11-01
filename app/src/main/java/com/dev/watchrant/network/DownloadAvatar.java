package com.dev.watchrant.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.dev.watchrant.animations.Tools;

import java.io.InputStream;

public class DownloadAvatar extends AsyncTask<String, Void, Bitmap> {

    String name;
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        this.name = urls[1];
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
        Tools.saveToInternalStorage(name,adjust(result));
    }

    private Bitmap adjust(Bitmap src)
    {
        // int to = Color.BLACK;
        int to = Color.alpha(0);

        //Need to copy to ensure that the bitmap is mutable.


        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++)
                if(match(bitmap.getPixel(x, y)))
                    bitmap.setPixel(x, y, to);

        return bitmap;
    }
    private static final int[] FROM_COLOR = new int[]{123,200,164};
    private static final int THRESHOLD = 40;
    private boolean match(int pixel)
    {
        //There may be a better way to match, but I wanted to do a comparison ignoring
        //transparency, so I couldn't just do a direct integer compare.
        return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD &&
                Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
                Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
    }
}
