package com.dev.watchrant;

import static com.dev.watchrant.MainActivity.sort;
import static com.dev.watchrant.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.activity.ConfirmationActivity;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.ConfirmationOverlay;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.databinding.ActivityOptionBinding;
import com.dev.watchrant.methods.MethodsSearch;
import com.dev.watchrant.methods.MethodsUpdate;
import com.dev.watchrant.models.ModelSearch;
import com.dev.watchrant.models.ModelUpdate;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionActivity extends Activity {

    private ActivityOptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createFeedList();
    }

    public void createFeedList(){
        ArrayList<OptionsItem> menuItems = new ArrayList<>();
        menuItems.add(new OptionsItem(null,"feed",1));
        menuItems.add(new OptionsItem(null,"TOP",0));
        menuItems.add(new OptionsItem(null,"RECENT",0));
        menuItems.add(new OptionsItem(null,"ALGO",0));
        menuItems.add(new OptionsItem(null,"other",1));
        menuItems.add(new OptionsItem(null,"SEARCH",0));
        menuItems.add(new OptionsItem(null,"settings",1));
        // Coming soon! menuItems.add(new OptionsItem(null,"MY PROFILE",0));
        // Coming soon! menuItems.add(new OptionsItem(null,"LOGIN",0));
        String versionName = BuildConfig.VERSION_NAME;
        menuItems.add(new OptionsItem(null,"VERSION "+versionName,0));
        menuItems.add(new OptionsItem(null,"UPDATE",0));
        menuItems.add(new OptionsItem(null,"information",1));
        menuItems.add(new OptionsItem(null,"tip: if you want OPEN ON PHONE to open in devRant go to:\n\nphone/settings/apps/devRant/setAsDefault/webAddresses\n\nand enable all",1));
        menuItems.add(new OptionsItem(null,"the update btn uses a github template api",1));
        build(menuItems);
    }

    private void build(ArrayList<OptionsItem> menuItems) {
        WearableRecyclerView wearableRecyclerView = binding.settingsMenuView;

        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new OptionsAdapter(this, menuItems, new OptionsAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                OptionsItem menuItem = menuItems.get(menuPosition);
                Intent intent;
                switch (menuItem.getText()) {
                    case "TOP":
                    case "RECENT":
                    case "ALGO":
                        sort = menuItem.getText().toLowerCase(Locale.ROOT);
                        intent = new Intent(OptionActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case "SEARCH":
                        intent = new Intent(OptionActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case "UPDATE":
                        checkUpdate();

                        break;
                    case "LOGIN":
                        intent = new Intent(OptionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                if (menuItem.getText().contains("VERSION")) {
                    int versionCode = BuildConfig.VERSION_CODE;
                    toast("version code "+versionCode);
                }


            }
        }));
    }


    private void checkUpdate() {
        MethodsUpdate methods = RetrofitClient.getRetrofitInstance().create(MethodsUpdate.class);
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/adress.json";

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Call<ModelUpdate> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelUpdate>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelUpdate> call, @NonNull Response<ModelUpdate> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    String version = response.body().getVersion();
                    int build = response.body().getBuild();
                    int versionCode = BuildConfig.VERSION_CODE;
                    if (versionCode < build) {
                        toast("Update available: Version "+version+" check phone!");

                        Executor executor = new Executor() {
                            @Override
                            public void execute(Runnable runnable) {

                            }
                        };
                        RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(OptionActivity.this, executor);

                        NodeClient client = Wearable.getNodeClient(OptionActivity.this);
                        client.getConnectedNodes().addOnSuccessListener(nodes -> {
                            if (nodes.size() > 0) {
                                String nodeId = nodes.get(0).getId();
                                ListenableFuture<Void> result = remoteActivityHelper.startRemoteActivity(
                                        new Intent(Intent.ACTION_VIEW)
                                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                                .setData(
                                                        Uri.parse("https://github.com/joewilliams007/watchRant")
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
                    } else {
                        toast("You have the latest version!");
                    }

                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {

                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelUpdate> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });

    }

    public void toast(String message) {
        Toast.makeText(OptionActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}