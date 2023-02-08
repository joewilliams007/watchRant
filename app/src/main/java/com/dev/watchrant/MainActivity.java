package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.profile_rants;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;
import static com.dev.watchrant.SearchActivity.search_rants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.SwipeDismissFrameLayout;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.MainMenuAdapter;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.animations.RantLoadingAnimation;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.Rants;
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
    RantLoadingAnimation rantLoadingAnimation;
    ProgressBar progressBar;
    View view;
    com.dev.watchrant.databinding.ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Account.theme().equals("dark")) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Amoled);
        }

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Boolean isSearch = intent.getBooleanExtra("isSearch",false);

        progressBar = findViewById(R.id.progressBar);

        if (isSearch) {
            toast("found "+search_rants.size()+" search results");
            createFeedList(search_rants);
        } else if (profile_rants!=null) {
            createFeedList(profile_rants);
            profile_rants = null;
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
        String total_url = BASE_URL + "devrant/rants?app=3&limit=50&sort="+sort+"&range=day&skip=0/";


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

                    createFeedList(rants);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {

                }

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
                progressBar.setVisibility(View.GONE);
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
        menuItems.add(new RantItem(null,"devRant",0,"avatar",0,0));
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
        WearableRecyclerView wearableRecyclerView = binding.mainMenuView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new MainMenuAdapter(this, menuItems, new MainMenuAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                RantItem menuItem = menuItems.get(menuPosition);

                if (menuItem.getType().equals("avatar")) {
                    if (sort.equals("recent")) {
                        sort = "top";
                        toast("top feed");
                        requestFeed();
                    } else {
                        sort = "recent";
                        toast("recent feed");
                        requestFeed();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, RantActivity.class);
                    intent.putExtra("id",String.valueOf(menuItem.getId()));
                    startActivity(intent);
                }
            }
        }));
    }

    public void toast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}