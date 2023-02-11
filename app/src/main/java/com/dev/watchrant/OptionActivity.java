package com.dev.watchrant;

import static com.dev.watchrant.MainActivity.sort;
import static com.dev.watchrant.RantActivity.openUrl;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.adapters.OptionsAdapter;
import com.dev.watchrant.adapters.OptionsItem;
import com.dev.watchrant.adapters.RantItem;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.Comment;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.databinding.ActivityOptionBinding;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.methods.MethodsUpdate;
import com.dev.watchrant.models.ModelRant;
import com.dev.watchrant.models.ModelUpdate;
import com.dev.watchrant.network.RetrofitClient;
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
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Account.theme().equals("dark")) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Amoled);
        }
        super.onCreate(savedInstanceState);

        binding = ActivityOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = findViewById(R.id.progressBar);
        createFeedList();
    }

    public void createFeedList(){
        ArrayList<OptionsItem> menuItems = new ArrayList<>();
        menuItems.add(new OptionsItem(null,"- feed -",1));
        menuItems.add(new OptionsItem(null,"TOP",0));
        menuItems.add(new OptionsItem(null,"RECENT",0));
        menuItems.add(new OptionsItem(null,"ALGO",0));
        menuItems.add(new OptionsItem(null,"- other -",1));
        menuItems.add(new OptionsItem(null,"SURPRISE ME",0));
        menuItems.add(new OptionsItem(null,"SEARCH",0));
        if (Account.isLoggedIn()) {
            menuItems.add(new OptionsItem(null,"NOTIF",0));
        }
        menuItems.add(new OptionsItem(null,"- settings -",1));
        menuItems.add(new OptionsItem(null,"MY PROFILE",0));

        if (Account.isLoggedIn()) {
            menuItems.add(new OptionsItem(null,"LOGOUT",0));
        } else {
            menuItems.add(new OptionsItem(null,"LOGIN",0));
            menuItems.add(new OptionsItem(null,"REGISTER",0));
        }
        menuItems.add(new OptionsItem(null,"THEME",0));
        menuItems.add(new OptionsItem(null,"ANIMATION",0));
        menuItems.add(new OptionsItem(null,"LIMIT",0));
        String versionName = BuildConfig.VERSION_NAME;
        menuItems.add(new OptionsItem(null,"VERSION "+versionName,0));
        menuItems.add(new OptionsItem(null,"UPDATE",0));
        menuItems.add(new OptionsItem(null,"- information -",1));
        menuItems.add(new OptionsItem(null,"tip: if you want OPEN ON PHONE to open in devRant go to:\n\nphone/settings/apps/devRant/setAsDefault/webAddresses\n\nand enable all",1));
        menuItems.add(new OptionsItem(null,"the update btn uses a github template api",1));

        menuItems.add(new OptionsItem(null,"CARTOONS",3));
        menuItems.add(new OptionsItem(null,"PODCASTS",3));
        menuItems.add(new OptionsItem(null,"TWITTER",3));
        menuItems.add(new OptionsItem(null,"FACEBOOK",3));
        menuItems.add(new OptionsItem(null,"ISSUE TRACKER",3));

        menuItems.add(new OptionsItem(null,"- credit -",1));
        menuItems.add(new OptionsItem(null,"Huge thanks to:",1));
        menuItems.add(new OptionsItem(null,"dfox & trogus\n(devRant team)",1));
        menuItems.add(new OptionsItem(null,"SIMMORSAL\n(rant animation)",1));
        menuItems.add(new OptionsItem(null,"Skayo & frogstair\n(api docs)",1));
        build(menuItems);
        menuItems.add(new OptionsItem(null,"- legal -",1));
        menuItems.add(new OptionsItem(null,"TERMS OF SERVICE*",3));
        menuItems.add(new OptionsItem(null,"PRIVACY POLICY*",3));
        menuItems.add(new OptionsItem(null,"*watchRant is not in any way affiliated with devRant."+
                "If you use watchRant however, you connect to devRants API and thereby need to agree to their privacy policy and terms of service.",1));
        menuItems.add(new OptionsItem(null,"By tapping on CHECK FOR UPDATE, a Github endpoint API is being accessed. No account or personal data is being sent.",1));
        menuItems.add(new OptionsItem(null,"This app is open source and therefore you can check and change anything that it does anyways.",1));
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
                    case "NOTIF":
                        intent = new Intent(OptionActivity.this, NotifActivity.class);
                        startActivity(intent);
                        break;
                    case "LOGIN":
                        intent = new Intent(OptionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case "LOGOUT":
                        logout();
                        break;
                    case "MY PROFILE":
                        if (Account.isLoggedIn()) {
                            intent = new Intent(OptionActivity.this, ProfileActivity.class);
                            intent.putExtra("id", String.valueOf(Account.user_id()));
                            startActivity(intent);
                        } else {
                            toast("please login first");
                        }
                        break;
                    case "SURPRISE ME":
                        getSurpriseId();
                        break;
                    case "THEME":
                        if (Account.theme().equals("dark")) {
                            Account.setTheme("amoled");
                        } else {
                            Account.setTheme("dark");
                        }
                        intent = new Intent(OptionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "ANIMATION":
                        if (Account.animate()) {
                            Account.setAnimate(false);
                            toast("animation disabled");
                        } else {
                            Account.setAnimate(true);
                            toast("animation enabled");
                        }
                        break;
                    case "CARTOONS":
                        toast("check phone");
                        openUrl("https://www.youtube.com/devrantapp");
                        break;
                    case "PODCASTS":
                        toast("check phone");
                        openUrl("https://soundcloud.com/devrantapp");
                        break;
                    case "TWITTER":
                        toast("check phone");
                        openUrl("https://twitter.com/devrantapp/");
                        break;
                    case "FACEBOOK":
                        toast("check phone");
                        openUrl("https://www.facebook.com/devrantapp/");
                        break;
                    case "ISSUE TRACKER":
                        toast("check phone");
                        openUrl("https://github.com/joewilliams007/watchRant");
                        break;
                    case "TERMS OF SERVICE*":
                        toast("check phone");
                        openUrl("https://devrant.com/feed/top/month?signup=1");
                        break;
                    case "PRIVACY POLICY*":
                        toast("check phone");
                        openUrl("https://devrant.com/privacy");
                        break;
                    case "REGISTER":
                        toast("check phone");
                        openUrl("https://devrant.com/terms");
                        break;
                    case "LIMIT":
                        if (Account.limit() == 10) {
                            Account.setLimit(20);
                        } else if (Account.limit() == 20) {
                            Account.setLimit(30);
                        } else if (Account.limit() == 30) {
                            Account.setLimit(40);
                        } else if (Account.limit() == 40) {
                            Account.setLimit(50);
                        } else {
                            Account.setLimit(10);
                        }

                        toast(Account.limit()+" rants");
                        break;
                }
                if (menuItem.getText().contains("VERSION")) {
                    int versionCode = BuildConfig.VERSION_CODE;
                    toast("version code "+versionCode);
                }


            }
        }));
    }

    private void getSurpriseId() { // Get random Rant. Need to make 2 api calls cuz comments don't come with the surprise sadly
        MethodsRant methods = RetrofitClient.getRetrofitInstance().create(MethodsRant.class);
        String total_url;
        if (Account.isLoggedIn()) {
            total_url = BASE_URL + "devrant/rants/surprise?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "devrant/rants/surprise?app=3";
        }

        progressBar.setVisibility(View.VISIBLE);
        Call<ModelRant> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelRant>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelRant> call, @NonNull Response<ModelRant> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    String id = String.valueOf(response.body().getRant().getId());
                    Intent intent = new Intent(OptionActivity.this, RantActivity.class);
                    intent.putExtra("id",id); // whats it gonna be
                    startActivity(intent);

                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelRant> call, @NonNull Throwable t) {
                Log.d("no network", t.toString());
                toast(t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void logout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("logout")
                .setMessage("U sure u want to logout :(")
                .setCancelable(true)

                .setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        Account.setKey(null);
                        Account.setExpire_time(0);
                        Account.setUser_id(0);
                        Account.setId(0);

                        toast("logged out");
                        createFeedList();
                    }
                })

                .setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }


    private void checkUpdate() {
        MethodsUpdate methods = RetrofitClient.getRetrofitInstance().create(MethodsUpdate.class);
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/adress.json";

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
                       openUrl("");
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