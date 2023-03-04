package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.isImage;
import static com.dev.watchrant.ProfileActivity.profile_avatar;
import static com.dev.watchrant.ProfileActivity.rant_image;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.databinding.ActivityAvatarBinding;
import com.dev.watchrant.network.DownloadImageTask;

public class AvatarActivity extends Activity {

    private ImageView imageView;
    private ActivityAvatarBinding binding;
    int tapped = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageView = binding.imageView;

        if (isImage) {
            new DownloadImageTask(imageView)
                    .execute(rant_image);
        } else {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+profile_avatar).into(imageView);
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void download(View view) {
        tapped++;
        if (tapped == 1) {
            toast("tap two times to save to storage");
        }
        if (tapped == 3) {


        toast("downloaded image");
        if (isImage) {
            DownloadManager downloadManager =
                    (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(rant_image);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility
                    (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("watchRant");
            request.setDescription("image downloaded");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"watchRant.jpg");
            downloadManager.enqueue(request);

        } else {
            DownloadManager downloadManager =
                    (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://avatars.devrant.com/"+profile_avatar);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility
                    (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("watchRant");
            request.setDescription("image downloaded");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"watchRant.jpg");
            downloadManager.enqueue(request);

        }
        }

    }

    public void toast(String message) {
        Toast.makeText(AvatarActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}