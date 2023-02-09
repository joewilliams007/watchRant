package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.isImage;

import static com.dev.watchrant.ProfileActivity.rant_image;
import static com.dev.watchrant.ReplyActivity.uploaded_comment;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.RantAdapter;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.classes.Comment;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.databinding.ActivityRantBinding;
import com.dev.watchrant.methods.MethodsImgRant;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.models.ModelImgRant;
import com.dev.watchrant.models.ModelRant;
import com.dev.watchrant.network.RetrofitClient;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RantActivity extends Activity {

private ActivityRantBinding binding;

    String id;
    ArrayList<RantItem> menuItems;
    String rant_url;
    public static ArrayList<String> users_of_comments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Account.theme().equals("dark")) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Amoled);
        }
        super.onCreate(savedInstanceState);

         binding = ActivityRantBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        menuItems = new ArrayList<>();
        requestComments();
    }

    private void requestComments() {
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

                    menuItems.add(new RantItem(null,user_avatar,0, "avatar",0,0));
                    menuItems.add(new RantItem(null,rant.getUser_username()+" +"+rant.getUser_score(),rant.getUser_id(), "details",0,rant.getNum_comments()));
                    menuItems.add(new RantItem(null,rant.getText(),0,"rant",rant.getScore(),rant.getNum_comments()));

                    if (rant.getAttached_image().toString().contains("http")) {
                        String url = rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
                        menuItems.add(new RantItem(url,url,0, "image",0,0));
                    }

                    createFeedList(comments, menuItems);
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

            if (!users_of_comments.contains(comment.getUser_username())) {
                users_of_comments.add(comment.getUser_username());
            }

            menuItems.add(new RantItem(null,comment.getUser_username()+" +"+comment.getUser_score(),0, "details",0,0));
            menuItems.add(new RantItem(null,s,comment.getUser_id(),"comment",comment.getScore(), 0));

            if (comment.getAttached_image()!=null) {
                String url = comment.getAttached_image().toString().replace("{url=","").split(", width")[0].replace("\\\\","");
                menuItems.add(new RantItem(url,url,0, "image",0,0));
            }
        }
        menuItems.add(new RantItem(null,"REPLY",0, "reply",0,0));
        menuItems.add(new RantItem(null,"OPEN ON PHONE",0, "phone",0,0));
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


                if (menuItem.getType().equals("image")) {
                    isImage = true;
                    rant_image = menuItem.getImage();
                    Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
                    startActivity(intent);
                } else if (menuItem.getType().equals("phone")){
                    toast("launching on phone");
                    openUrl("https://devrant.com/"+rant_url);
                } else if (menuItem.getId() != 0) {
                    isImage = false;
                    Intent intent = new Intent(RantActivity.this, ProfileActivity.class);
                    intent.putExtra("id", String.valueOf(menuItem.getId()));
                    startActivity(intent);
                } else if (menuItem.getType().equals("avatar")) {
                    Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
                    startActivity(intent);
                } else if (menuItem.getType().equals("reply")) {
                    Intent intent = new Intent(RantActivity.this, ReplyActivity.class);
                    intent.putExtra("id", String.valueOf(id));
                    startActivity(intent);
                }
            }
        }));
    }

    public static void toast(String message) {
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void openUrl(String url) {
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