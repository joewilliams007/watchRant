package com.dev.watchrant;

import static com.dev.watchrant.ProfileActivity.profile_rants;
import static com.dev.watchrant.RetrofitClient.BASE_URL;
import static com.dev.watchrant.SearchActivity.search_rants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.widget.SwipeDismissFrameLayout;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.databinding.ActivityMainBinding;
import com.dev.watchrant.methods.MethodsFeed;
import com.dev.watchrant.models.ModelFeed;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    public static String sort = "recent";

    com.dev.watchrant.databinding.ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Boolean isSearch = intent.getBooleanExtra("isSearch",false);

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
        MethodsFeed methods = RetrofitClient.getRetrofitInstance().create(MethodsFeed.class);
        String total_url = BASE_URL + "devrant/rants?app=3&limit=50&sort="+sort+"&range=day&skip=0/";

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

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

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelFeed> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
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