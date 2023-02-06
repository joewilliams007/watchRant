package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.isImage;
import static com.dev.watchrant.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.databinding.ActivityRantBinding;
import com.dev.watchrant.methods.MethodsImgRant;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.models.ModelImgRant;
import com.dev.watchrant.models.ModelRant;
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
    String image_url = null;
    String rant_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         binding = ActivityRantBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        menuItems = new ArrayList<>();
        requestImage();
        requestComments();

    }

    private void requestComments() {
        MethodsRant methods = RetrofitClient.getRetrofitInstance().create(MethodsRant.class);
        String total_url = BASE_URL + "devrant/rants/"+id+"?app=3";

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

                    if (image_url != null) {
                        menuItems.add(new RantItem(null,image_url,0, "image",0,0));
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

    private void requestImage() {
        try {

        MethodsImgRant methods = RetrofitClient.getRetrofitInstance().create(MethodsImgRant.class);
        String total_url = BASE_URL + "devrant/rants/"+id+"?app=3";

        Call<ModelImgRant> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelImgRant>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelImgRant> call, @NonNull Response<ModelImgRant> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    String image = response.body().getImgRant().getAttached_image().getUrl().replaceAll("\\\\","");
                    image_url = image;
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<ModelImgRant> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
            }
        });

        } catch (Exception e){

        }
    }

    public void createFeedList(List<Comment> comments, ArrayList<RantItem> menuItems){


        for (Comment comment : comments){
            String s = comment.getBody();

            menuItems.add(new RantItem(null,comment.getUser_username()+" +"+comment.getUser_score(),0, "details",0,0));
            menuItems.add(new RantItem(null,s,comment.getUser_id(),"comment",comment.getScore(), 0));
        }

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
                    Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
                    startActivity(intent);
                } else if (menuItem.getType().equals("phone")){
                    openUrl("https://devrant.com/"+rant_url);
                } else if (menuItem.getId() != 0) {
                    isImage = false;
                    Intent intent = new Intent(RantActivity.this, ProfileActivity.class);
                    intent.putExtra("id", String.valueOf(menuItem.getId()));
                    startActivity(intent);
                } else if (menuItem.getType().equals("avatar")) {
                    Intent intent = new Intent(RantActivity.this, AvatarActivity.class);
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