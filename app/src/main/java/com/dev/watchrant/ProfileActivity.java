package com.dev.watchrant;

import static com.dev.watchrant.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.databinding.ActivityProfileBinding;
import com.dev.watchrant.methods.MethodsProfile;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.models.ModelProfile;
import com.dev.watchrant.models.ModelRant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends Activity {

    private ActivityProfileBinding binding;
    public static List<Rants> profile_rants;
    public static String profile_avatar;
    public static String rant_image;
    public static Boolean isImage = false;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        isImage = false;
        requestProfile();

    }

    private void requestProfile() {
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url = BASE_URL + "users/"+id+"?app=3";

        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    String user_avatar = response.body().getProfile().getAvatar().getI();
                    String username = response.body().getProfile().getUsername();
                    int score = response.body().getProfile().getScore();
                    String about = response.body().getProfile().getAbout();
                    String location = response.body().getProfile().getLocation();
                    int created_time = response.body().getProfile().getCreated_time();
                    String skills = response.body().getProfile().getSkills();
                    String github = response.body().getProfile().getGithub();
                    String website = response.body().getProfile().getWebsite();
                    User_avatar avatar = response.body().getProfile().getAvatar();
                    Counts counts = response.body().getProfile().getContent().getCounts();
                    int rants_count = counts.getRants();
                    int upvoted_count = counts.getUpvoted();
                    int comments_count = counts.getComments();
                    int favorites_count = counts.getFavorites();
                    int collabs_count = counts.getCollabs();
                    profile_rants = response.body().getProfile().getContent().getContent().getRants();
                    profile_avatar = user_avatar;
                    ArrayList<RantItem> menuItems = new ArrayList<>();

                    menuItems.add(new RantItem(null,user_avatar,0, "avatar",0,0));
                    menuItems.add(new RantItem(null,username+" +"+score,0, "details",0,0));

                    if (about.length()>0) {
                        menuItems.add(new RantItem(null,about,0,"info",0,0));
                    }
                    if (github.length()>0) {
                        menuItems.add(new RantItem(null,
                                "github: "+github ,0,"info",0,0));
                    }
                    if (website.length()>0) {
                        menuItems.add(new RantItem(null,
                                "website: "+website ,0,"info",0,0));
                    }
                    if (location.length()>0) {
                        menuItems.add(new RantItem(null,
                                "location: "+location ,0,"info_small",0,0));
                    }
                    menuItems.add(new RantItem(null,
                                    "rants: "+rants_count
                                    +"\nupvoted: "+upvoted_count
                                    +"\ncomments: "+upvoted_count
                                    +"\nfavorites: "+favorites_count
                                    +"\ncollabs: "+collabs_count,0,"info_small",0,0));

                    if (skills.length()>0) {
                        menuItems.add(new RantItem(null, "skills: "+skills,0,"info_small",0,0));
                    }
                    menuItems.add(new RantItem(null,"RANTS",0, "info",0,0));

                    createFeedList(profile_rants,menuItems);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<ModelProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast(t.toString());
            }
        });
    }


    public void createFeedList(List<Rants> rants,ArrayList<RantItem> menuItems){
        for (Rants rant : rants){
            String s = rant.getText();
            if (s.length()>100) {
                s = s.substring(0, Math.min(s.length(), 100))+"...";
            }

            menuItems.add(new RantItem(null,s,rant.getId(),"feed",rant.getScore(), rant.getNum_comments()));
        }
        build(menuItems);
    }

    private void build(ArrayList<RantItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.profileView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        wearableRecyclerView.setAdapter(new ProfileAdapter(this, menuItems, new ProfileAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                RantItem menuItem = menuItems.get(menuPosition);
                if(menuItem.getText().equals("RANTS")) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                if(menuItem.getType().equals("avatar")) {
                    Intent intent = new Intent(ProfileActivity.this, AvatarActivity.class);
                    startActivity(intent);
                }
            }
        }));


    }

    public void toast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}