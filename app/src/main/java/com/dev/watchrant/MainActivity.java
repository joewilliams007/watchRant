package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.profile_rants;
import static com.dev.watchrant.RantActivity.vibrate;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;
import static com.dev.watchrant.SearchActivity.search_rants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewConfigurationCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.SwipeDismissFrameLayout;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.MainMenuAdapter;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.animations.RantLoadingAnimation;
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.auth.MyApplication;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.classes.attached_image;
import com.dev.watchrant.databinding.ActivityMainBinding;
import com.dev.watchrant.methods.MethodsFeed;
import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    public static String sort = "recent";
    int notif_amount;
    RantLoadingAnimation rantLoadingAnimation;
    ProgressBar progressBar;
    View view;
    ImageView imageView;
    com.dev.watchrant.databinding.ActivityMainBinding binding;
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Boolean isSearch = intent.getBooleanExtra("isSearch",false);
        wearableRecyclerView = binding.mainMenuView;

        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        if (!Account.theme().equals("dark")) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.empty_screen_full_black1));
        }
        imageView.setVisibility(View.VISIBLE);
        wearableRecyclerView.setVisibility(View.GONE);

        if (isSearch) {
            toast("found "+search_rants.size()+" search results");
            createFeedList(search_rants);
        } else {
            requestFeed();
        }

        SwipeDismissFrameLayout swipeDismissFrameLayout = findViewById(R.id.swipeFeed);
        swipeDismissFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onSwipeStarted(SwipeDismissFrameLayout layout) {
                super.onSwipeStarted(layout);
            }

            @Override
            public void onSwipeCanceled(SwipeDismissFrameLayout layout) {
                super.onSwipeCanceled(layout);
            }

            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                super.onDismissed(layout);
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestFeed() {
        if (Account.animate()) {
            rantLoadingAnimation = new RantLoadingAnimation((RelativeLayout) findViewById(R.id.relContainer));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startReq();
                }
            }, 0000);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.GONE);
            startReq();
        }
    }

    private void startReq() {
        MethodsFeed methods = RetrofitClient.getRetrofitInstance().create(MethodsFeed.class);
        String total_url;
        if (Account.isLoggedIn()) {
            total_url = BASE_URL
                    + "devrant/rants?&"+"token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&app=3&limit="+Account.limit()+"&sort="+sort+"&range=day&skip=0"
                    +"/";

        } else {
            total_url = BASE_URL
                    + "devrant/rants?app=3&limit="+Account.limit()+"&sort="+sort+"&range=day&skip=0/";
        }


        Call<ModelFeed> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelFeed>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelFeed> call, @NonNull Response<ModelFeed> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<Rants> rants = response.body().getRants();
                    //   toast("success: "+success+" size: "+rants.size());
                    if (Account.isLoggedIn()) {
                        notif_amount = response.body().getUnread().getTotal();
                    }
                    createFeedList(rants);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                }

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                wearableRecyclerView.setVisibility(View.VISIBLE);
                wearableRecyclerView.requestFocus();

            }

            @Override
            public void onFailure(@NonNull Call<ModelFeed> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void createFeedList(List<Rants> rants){
        ArrayList<RantItem> menuItems = new ArrayList<>();
        menuItems.add(new RantItem(null,"devRant",0,"avatar",0,0,0,null,0));

        // Can be added but takes up space idk u decide! menuItems.add(new RantItem(null,sort,0,"type",0, 0));
        if (Account.isLoggedIn()) {
            menuItems.add(new RantItem(null,null,0,"notif",notif_amount, 0,0,null,0));
        }

        String[] blockedWords = Account.blockedWords().split(",");
        String[] blockedUsers = Account.blockedUsers().split(",");
        for (Rants rant : rants){
            String s = rant.getText();
            String username = rant.getUser_username().toLowerCase();

            String[] tags = rant.getTags();
            String t = "";
            for (String tag: tags) {
                t+=tag;
            }

            String s_check = s.toLowerCase()+t.toLowerCase();
            boolean containsBlocked = false;

            if (Account.blockGreenDot()) {
                if (rant.getUser_avatar().getI()==null) {
                    containsBlocked = true;
                }
            }

            if (Account.blockedWords()!=null&&!Account.blockedWords().equals("") && !containsBlocked) {
                for (String word : blockedWords) {
                    if (s_check.contains(word)) {
                        containsBlocked = true;
                        break;
                    }
                }
            }
            if (Account.blockedUsers()!=null&&!Account.blockedUsers().equals("") && !containsBlocked) {
                for (String user : blockedUsers) {
                    if (username.equals(user.toLowerCase())) {
                        containsBlocked = true;
                        break;
                    }
                }
            }

            if (!containsBlocked) {

                if (s.length() > 100) {
                    s = s.substring(0, Math.min(s.length(), 100)) + "...";
                }

                if (rant.getAttached_image().toString().contains("http")) {
                    menuItems.add(new RantItem(null, s, rant.getId(), "feed", rant.getScore(), rant.getNum_comments(), 0, null, rant.getVote_state()));
                    menuItems.add(new RantItem(null, s, rant.getId(), "image", rant.getScore(), rant.getNum_comments(), 0, null, rant.getVote_state()));
                } else {
                    menuItems.add(new RantItem(null, s, rant.getId(), "feed", rant.getScore(), rant.getNum_comments(), 0, null, rant.getVote_state()));
                }
            }

        }
        build(menuItems);
    }

    private void build(ArrayList<RantItem> menuItems) {


        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView wearableRecyclerView, int newState) {
                super.onScrollStateChanged(wearableRecyclerView, newState);

                if (!wearableRecyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----","end");
                    vibrate();
                }
            }
        });

        wearableRecyclerView.setAdapter(new MainMenuAdapter(this, menuItems, new MainMenuAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                RantItem menuItem = menuItems.get(menuPosition);

                switch (menuItem.getType()) {
                    case "avatar":
                    case "type": {
                        vibrate();
                        Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "notif": {
                        Intent intent = new Intent(MainActivity.this, NotifActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default: {
                        Intent intent = new Intent(MainActivity.this, RantActivity.class);
                        intent.putExtra("id", String.valueOf(menuItem.getId()));
                        startActivity(intent);
                        break;
                    }
                }
            }
        }));
    }

    public void toast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}