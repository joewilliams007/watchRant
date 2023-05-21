package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.isImage;

import static com.dev.watchrant.ProfileActivity.rant_image;
import static com.dev.watchrant.ReplyActivity.replyText;
import static com.dev.watchrant.ReplyActivity.uploaded_comment;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.activity.ConfirmationActivity;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.RantAdapter;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.classes.Comment;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.databinding.ActivityRantBinding;
import com.dev.watchrant.methods.MethodsImgRant;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.models.ModelImgRant;
import com.dev.watchrant.models.ModelRant;
import com.dev.watchrant.models.ModelSuccess;
import com.dev.watchrant.network.RetrofitClient;
import com.dev.watchrant.post.CommentClient;
import com.dev.watchrant.post.VoteClient;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RantActivity extends Activity {

private ActivityRantBinding binding;

    String id;
    ArrayList<RantItem> menuItems;
    ProgressBar progressBar;
    String rant_url;
    int rantVote = 0;
    public static ArrayList<String> users_of_comments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

         binding = ActivityRantBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        replyText = ""; // reset reply text because 4sure its a new rant so we don't need the old reply text
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        requestComments();
    }

    private void requestComments() {

        menuItems = new ArrayList<>();
        MethodsRant methods = RetrofitClient.getRetrofitInstance().create(MethodsRant.class);
        String total_url;
        if (Account.isLoggedIn()) {
            total_url = BASE_URL + "devrant/rants/"+id+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "devrant/rants/"+id+"?app=3";
        }
        Call<ModelRant> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelRant>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelRant> call, @NonNull Response<ModelRant> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Rants rant = response.body().getRant();
                    String user_avatar = response.body().getRant().getUser_avatar().getI();
                    rant_url = response.body().getRant().getLink();
                    List<Comment> comments = response.body().getComments();
                    //   toast("success: "+success+" size: "+rants.size());

                    menuItems.add(new RantItem(null,user_avatar,0, "avatar",0,0,0,null,0));
                    menuItems.add(new RantItem(null,rant.getUser_username()+" +"+rant.getUser_score(),rant.getUser_id(), "details",0,rant.getNum_comments(),rant.getCreated_time(),null,rant.getVote_state()));
                    rantVote = rant.getVote_state();
                    if (Account.user_id() == rant.getUser_id()) {
                        rantVote = 0;
                    }
                    if (rant.getText().contains("\n")) {
                        String[] splitRant = rant.getText().split("\n");
                        for (String text: splitRant) {
                            if (text.length()>0 ){
                                menuItems.add(new RantItem(null,text,0,"rant",rant.getScore(),rant.getNum_comments(),rant.getCreated_time(),rant.getUser_username(),rantVote));
                            }
                        }
                    } else {
                        menuItems.add(new RantItem(null,rant.getText(),0,"rant",rant.getScore(),rant.getNum_comments(),rant.getCreated_time(),rant.getUser_username(),rantVote));
                    }
                    menuItems.add(new RantItem(null,"++ UPVOTE",0, "++",0,0,0,null,0));
                    menuItems.add(new RantItem(null,"-- DOWNVOTE",0, "--",0,0,0,null,0));

                    menuItems.add(new RantItem(null,"amount",0,"amount",rant.getScore(),rant.getNum_comments(),rant.getCreated_time(),rant.getUser_username(),rantVote));

                    if (rant.getAttached_image().toString().contains("http")) {
                        String url = rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
                        menuItems.add(new RantItem(url,url,0, "image",0,0,0,null,0));
                    }

                    createFeedList(comments, menuItems);
                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<ModelRant> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast(t.toString());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (uploaded_comment) {
            menuItems = new ArrayList<>();
            requestComments();
            uploaded_comment = false;
        }
    }

    public void createFeedList(List<Comment> comments, ArrayList<RantItem> menuItems){
        for (Comment comment : comments){
            String s = comment.getBody();
            int rantVote = comment.getVote_state();
            if (Account.user_id() == comment.getUser_id()) {
                rantVote = 0;
            }
            menuItems.add(new RantItem(null,comment.getUser_username()+" "+comment.getUser_score(),comment.getUser_id(),"details",0,0,comment.getCreated_time(),comment.getUser_username(),rantVote));

            if (s.contains("\n")) {
                String[] splitRant = s.split("\n");
                for (String text: splitRant) {
                    if (text.length()>0 ){
                        menuItems.add(new RantItem(null,text,comment.getUser_id(),"comment",comment.getScore(),0,comment.getCreated_time(),comment.getUser_username(),rantVote));
                    }
                }
            } else {
                menuItems.add(new RantItem(null,s,comment.getUser_id(),"comment",comment.getScore(),0,comment.getCreated_time(),comment.getUser_username(),rantVote));
            }
            if (comment.getAttached_image()!=null) {
                String url = comment.getAttached_image().toString().replace("{url=","").split(", width")[0].replace("\\\\","");
                menuItems.add(new RantItem(url,url,comment.getUser_id(), "image",0,0,comment.getCreated_time(),comment.getUser_username(),comment.getVote_state()));
            }

            menuItems.add(new RantItem(null,"amount",0,"amountComment",comment.getScore(),0,comment.getCreated_time(),comment.getUser_username(),rantVote));
        }

        menuItems.add(new RantItem(null,"REPLY",0, "reply",0,0,0,null,0));

        menuItems.add(new RantItem(null,"OPEN ON PHONE",0, "phone",0,0,0,null,0));
        build(menuItems);
    }

    private void build(ArrayList<RantItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.rantView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new RantAdapter(this, menuItems, new RantAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                RantItem menuItem = menuItems.get(menuPosition);


                switch (menuItem.getType()) {
                    case "image": {
                        isImage = true;
                        rant_image = menuItem.getImage();
                        Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "phone":
                        openUrl("https://devrant.com/" + rant_url);
                        break;
                    case "comment":
                        if (Account.isLoggedIn()) {
                            replyText += "@" + menuItem.getUsername() + " ";
                            Intent intent = new Intent(RantActivity.this, ReplyActivity.class);
                            intent.putExtra("id", String.valueOf(id));
                            startActivity(intent);
                        } else {
                            toast("please login first");
                        }
                        break;
                    case "details": {
                        isImage = false;
                        Intent intent = new Intent(RantActivity.this, ProfileActivity.class);
                        intent.putExtra("id", String.valueOf(menuItem.getId()));
                        startActivity(intent);
                        break;
                    }
                    case "avatar": {
                        Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "reply":
                        if (Account.isLoggedIn()) {
                            Intent intent = new Intent(RantActivity.this, ReplyActivity.class);
                            intent.putExtra("id", String.valueOf(id));
                            startActivity(intent);
                        } else {
                            toast("please login first");
                        }
                        break;
                    case "++":
                        if (Account.isLoggedIn()) {
                            if (rantVote == 1) {
                                votePost(0);
                            } else {
                                votePost(1);
                            }
                        } else {
                            toast("please login first");
                        }
                        break;
                    case "--":
                        if (Account.isLoggedIn()) {
                            if (rantVote == -1) {
                                votePost(0);
                            } else {
                                votePost(-1);
                            }
                        } else {
                            toast("please login first");
                        }
                        break;
                }
            }
        }));
        wearableRecyclerView.requestFocus();
    }

    private void votePost(int i) {
        vibrate();
        progressBar.setVisibility(View.VISIBLE);
        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody vote = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(i));
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            VoteClient client = retrofit.create(VoteClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.vote(app, vote, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            requestComments();
                        } else {
                            toast("failed");
                        }
                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("You are not authorized :P");
                    } else {
                        toast(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                }

            });
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public static void toast(String message) {
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void vibrate() {
        if (!Account.vibrate()) {
            return;
        }
        Vibrator vibrator;
        VibratorManager vibratorManager;
        long[] VIBRATE_PATTERN = {500, 500};
        if (Build.VERSION.SDK_INT>=31) {
            vibratorManager = (VibratorManager) MyApplication.getAppContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        }
        else {
            vibrator = (Vibrator) MyApplication.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

        // vibrator.vibrate(VibrationEffect.createWaveform(VIBRATE_PATTERN,0));
        try {
            vibrator.vibrate(VibrationEffect.createOneShot(10,255));
        } catch (Exception e) {
            System.out.println("vibration failed "+e.getMessage());
        }

    }
    public static void openUrl(String url) {
        Intent intent = new Intent(MyApplication.getAppContext(), ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(intent);
        vibrate();
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable runnable) {

            }
        };
        RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(MyApplication.getAppContext(), executor);

        NodeClient client = Wearable.getNodeClient(MyApplication.getAppContext());
        client.getConnectedNodes().addOnSuccessListener(nodes -> {
            if (nodes.size() > 0) {
                String nodeId = nodes.get(0).getId();
                ListenableFuture<Void> result = remoteActivityHelper.startRemoteActivity(
                        new Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(
                                        Uri.parse(url)
                                )
                        , nodeId);
                result.addListener(() -> {
                    try {
                        result.get();
                    } catch (Exception e) {
                        toast("Failed " + e);
                    }
                }, executor);
            } else {
                toast("no connected wear watch");
            }
        }).addOnFailureListener(failure -> {
            toast("failed "+failure.getMessage());
        });
    }
}