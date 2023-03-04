package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.openUrl;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.ProfileAdapter;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.Counts;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.classes.User_avatar;
import com.dev.watchrant.databinding.ActivityProfileBinding;
import com.dev.watchrant.methods.MethodsProfile;
import com.dev.watchrant.models.ModelProfile;
import com.dev.watchrant.network.RetrofitClient;

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
    public String username;
    String github;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
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
        String total_url;
        if (Account.isLoggedIn()){
            total_url = BASE_URL + "users/"+id+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "users/"+id+"?app=3";
        }
        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    String user_avatar = response.body().getProfile().getAvatar().getI();
                    username = response.body().getProfile().getUsername();
                    int score = response.body().getProfile().getScore();
                    String about = response.body().getProfile().getAbout();
                    String location = response.body().getProfile().getLocation();
                    int created_time = response.body().getProfile().getCreated_time();
                    String skills = response.body().getProfile().getSkills();
                    github = response.body().getProfile().getGithub();
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

                    menuItems.add(new RantItem(null,user_avatar,0, "avatar",0,0,0,null,0));
                    menuItems.add(new RantItem(null,username+" +"+score,0, "details",0,0,0,null,0));

                    if (about.length()>0) {
                        menuItems.add(new RantItem(null,about,0,"info",0,0,0,null,0));
                    }
                    if (github.length()>0) {
                        menuItems.add(new RantItem(null,
                                "github: "+github ,0,"phone",0,0,0,null,0));
                    }
                    if (website.length()>0) {
                        menuItems.add(new RantItem(null,website,0, "phone",0,0,0,null,0));
                    }
                    if (location.length()>0) {
                        menuItems.add(new RantItem(null,
                                "location: "+location ,0,"info_small",0,0,0,null,0));
                    }
                    menuItems.add(new RantItem(null,
                                    "rants: "+rants_count
                                    +"\nupvoted: "+upvoted_count
                                    +"\ncomments: "+upvoted_count
                                    +"\nfavorites: "+favorites_count
                                    +"\ncollabs: "+collabs_count,0,"info_small",0,0,0,null,0));

                    if (skills.length()>0) {
                        menuItems.add(new RantItem(null, "skills: "+skills,0,"info_small",0,0,0,null,0));
                    }
                    menuItems.add(new RantItem(null,"OPEN ON PHONE",0, "phone",0,0,0,null,0));
                    menuItems.add(new RantItem(null,"RANTS",0, "info",0,0,0,null,0));

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
            int rantVote = rant.getVote_state();
            if (Account.user_id() == rant.getUser_id()) {
                rantVote = 0;
            }
            menuItems.add(new RantItem(null,s,rant.getId(),"feed",rant.getScore(), rant.getNum_comments(),rant.getCreated_time()*1000L,null,rantVote));
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
                switch (menuItem.getType()) {
                    case "avatar": {
                        Intent intent = new Intent(ProfileActivity.this, AvatarActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "phone":
                        toast("launching on phone");
                        if (menuItem.getText().equals("OPEN ON PHONE")) {
                            openUrl("https://devrant.com/users/" + username);
                        } else if (menuItem.getText().contains("github:")) {
                            openUrl("https://github.com/" + github);
                        } else {
                            openUrl(menuItem.getText());
                        }
                        break;
                    case "feed": {
                        Intent intent = new Intent(ProfileActivity.this, RantActivity.class);
                        intent.putExtra("id", String.valueOf(menuItem.getId()));
                        startActivity(intent);
                        break;
                    }
                }
            }
        }));


    }

    public void toast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}