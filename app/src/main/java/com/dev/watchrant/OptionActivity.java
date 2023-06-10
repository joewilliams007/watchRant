package com.dev.watchrant;

import static com.dev.watchrant.MainActivity.sort;
import static com.dev.watchrant.RantActivity.openUrl;
import static com.dev.watchrant.RantActivity.vibrate;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;
import static com.dev.watchrant.network.RetrofitClient.SKY_SERVER_URL;

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
import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.Comment;
import com.dev.watchrant.classes.Counts;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.classes.User_avatar;
import com.dev.watchrant.classes.sky.SkyProfile;
import com.dev.watchrant.databinding.ActivityOptionBinding;
import com.dev.watchrant.methods.MethodsProfile;
import com.dev.watchrant.methods.MethodsRant;
import com.dev.watchrant.methods.MethodsUpdate;
import com.dev.watchrant.methods.git.MethodsVerifyGithub;
import com.dev.watchrant.methods.sky.MethodsSkyProfile;
import com.dev.watchrant.methods.sky.MethodsVerifySkyKey;
import com.dev.watchrant.models.ModelProfile;
import com.dev.watchrant.models.ModelRant;
import com.dev.watchrant.models.ModelUpdate;
import com.dev.watchrant.models.git.ModelVerifyGithub;
import com.dev.watchrant.models.sky.ModelSkyProfile;
import com.dev.watchrant.models.sky.ModelSuccess;
import com.dev.watchrant.models.sky.ModelVerifySkyKey;
import com.dev.watchrant.network.DownloadAvatar;
import com.dev.watchrant.network.DownloadImageTask;
import com.dev.watchrant.network.RetrofitClient;
import com.dev.watchrant.post.CommentClient;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OptionActivity extends Activity {

    private ActivityOptionBinding binding;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
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
            menuItems.add(new OptionsItem(null,"NEW RANT",0));
        }

        menuItems.add(new OptionsItem(null,"- settings -",1));
        menuItems.add(new OptionsItem(null,"MY PROFILE",0));

        if (Account.isLoggedIn()) {
            menuItems.add(new OptionsItem(null,"LOGOUT",0));
        } else {
            menuItems.add(new OptionsItem(null,"LOGIN",0));
            menuItems.add(new OptionsItem(null,"REGISTER",0));
        }

        menuItems.add(new OptionsItem(null,"SKY",0));

        menuItems.add(new OptionsItem(null,"THEME",0));
        menuItems.add(new OptionsItem(null,"WATCHFACE",0));
        menuItems.add(new OptionsItem(null,"ANIMATION",0));
        menuItems.add(new OptionsItem(null,"VIBRATION",0));
        menuItems.add(new OptionsItem(null,"LIMIT",0));
        String versionName = BuildConfig.VERSION_NAME;
        menuItems.add(new OptionsItem(null,"VERSION "+versionName,0));
        // menuItems.add(new OptionsItem(null,"UPDATE",0));
        menuItems.add(new OptionsItem(null,"- information -",1));
        menuItems.add(new OptionsItem(null,"tip: if you want OPEN ON PHONE to open in devRant navigate to:\n\nphone/settings/apps/devRant/setAsDefault/webAddresses\n\nand enable all URLs.",1));
        // menuItems.add(new OptionsItem(null,"the update btn uses a github template api",1));

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

        menuItems.add(new OptionsItem(null,"- GitHub -",1));
        menuItems.add(new OptionsItem(null,"watchRant",3));
        menuItems.add(new OptionsItem(null,"skyRant",3));
        menuItems.add(new OptionsItem(null,"skyAPI",3));
        menuItems.add(new OptionsItem(null,"- legal -",1));
        menuItems.add(new OptionsItem(null,"TERMS OF SERVICE*",3));
        menuItems.add(new OptionsItem(null,"PRIVACY POLICY*",3));
        menuItems.add(new OptionsItem(null,"*watchRant is not in any way affiliated with devRant. "+
                "If you use watchRant however, you connect to the API of devRant and there by need to agree to their privacy policy and terms of service.",1));
        // menuItems.add(new OptionsItem(null,"By tapping on CHECK FOR UPDATE, a Github endpoint API is being accessed. No account or personal data is being sent.",1));
        // menuItems.add(new OptionsItem(null,"This app is open source and can be found on Github.",1));

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
                    case "NEW RANT":
                        if (Account.isLoggedIn()) {
                            toast("select type");
                            createChooseList();
                        } else {
                            toast("please login first");
                        }
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
                        } else if (Account.theme().equals("amoled")){
                            Account.setTheme("amoled_part");
                        } else if (Account.theme().equals("amoled_part")){
                            Account.setTheme("green");
                        } else if (Account.theme().equals("green")){
                            Account.setTheme("discord");
                        } else if (Account.theme().equals("discord")){
                            Account.setTheme("coffee");
                        } else if (Account.theme().equals("coffee")){
                            Account.setTheme("dark");
                        }

                        Tools.setTheme(OptionActivity.this);
                        intent = new Intent(OptionActivity.this, OptionActivity.class);
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
                        vibrate();
                        break;
                    case "VIBRATION":
                        if (Account.vibrate()) {
                            Account.setVibrate(false);
                            toast("vibration disabled");
                        } else {
                            Account.setVibrate(true);
                            toast("vibration enabled");
                        }
                        vibrate();
                        break;
                    case "WATCHFACE":
                        if (Account.isLoggedIn()) {
                            requestAvatar();
                        } else {
                            toast("pleas login first");
                        }
                        vibrate();
                        break;
                    case "CARTOONS":
                        openUrl("https://www.youtube.com/devrantapp");
                        break;
                    case "PODCASTS":
                        openUrl("https://soundcloud.com/devrantapp");
                        break;
                    case "TWITTER":
                        openUrl("https://twitter.com/devrantapp/");
                        break;
                    case "FACEBOOK":
                        openUrl("https://www.facebook.com/devrantapp/");
                        break;
                    case "ISSUE TRACKER":
                        openUrl("https://github.com/joewilliams007/watchRant");
                        break;
                    case "TERMS OF SERVICE*":
                        openUrl("https://devrant.com/terms");
                        break;
                    case "PRIVACY POLICY*":
                        openUrl("https://devrant.com/privacy");
                        break;
                    case "REGISTER":
                        openUrl("https://devrant.com/feed/top/month?signup=1");
                        break;
                    case "watchRant":
                        openUrl("https://github.com/joewilliams007/watchRant");
                        break;
                    case "skyRant":
                        openUrl("https://github.com/joewilliams007/skyRant");
                        break;
                    case "skyAPI":
                        openUrl("https://github.com/joewilliams007/skyAPI");
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
                    case "Rant/Story":
                        intent = new Intent(OptionActivity.this, WriteRantActivity.class);
                        intent.putExtra("type", "1");
                        startActivity(intent);
                        break;
                    case "Joke/Meme":
                        intent = new Intent(OptionActivity.this, WriteRantActivity.class);
                        intent.putExtra("type", "2");
                        startActivity(intent);
                        break;
                    case "Question":
                        intent = new Intent(OptionActivity.this, WriteRantActivity.class);
                        intent.putExtra("type", "3");
                        startActivity(intent);
                        break;
                    case "devRant":
                        intent = new Intent(OptionActivity.this, WriteRantActivity.class);
                        intent.putExtra("type", "5");
                        startActivity(intent);
                        break;
                    case "Random":
                        intent = new Intent(OptionActivity.this, WriteRantActivity.class);
                        intent.putExtra("type", "6");
                        startActivity(intent);
                        break;
                    case "SKY":
                        if (Account.isLoggedIn()) {
                            createSkyList();
                        } else {
                            toast("please login first");
                        }
                        break;
                    case "verify":
                        verify();
                        break;
                    case "sync following and blocked":
                        if (Account.isSessionSkyVerified()) {
                            syncSkyData();
                        } else {
                            toast("please verify first");
                        }
                        break;
                    case "following":
                        if (Account.isSessionSkyVerified()) {
                            intent = new Intent(OptionActivity.this, FollowingActivity.class);
                            startActivity(intent);
                        } else {
                            toast("please verify first");
                        }
                        break;
                }
                if (menuItem.getText().contains("VERSION")) {
                    int versionCode = BuildConfig.VERSION_CODE;
                    toast("version code "+versionCode);
                }


            }
        }));

        wearableRecyclerView.requestFocus();
    }




    private void createSkyList() {
        ArrayList<OptionsItem> menuItems = new ArrayList<>();
        menuItems.add(new OptionsItem(null,"sky cloud",1));
        menuItems.add(new OptionsItem(null,"verify",0));
        menuItems.add(new OptionsItem(null,"sync following and blocked",0));
        menuItems.add(new OptionsItem(null,"following",0));

        build(menuItems);
    }

    public void createChooseList() {
        ArrayList<OptionsItem> menuItems = new ArrayList<>();
        menuItems.add(new OptionsItem(null,"select type",1));
        menuItems.add(new OptionsItem(null,"Rant/Story",0));
        menuItems.add(new OptionsItem(null,"Joke/Meme",0));
        menuItems.add(new OptionsItem(null,"Question",0));
        menuItems.add(new OptionsItem(null,"devRant",0));
        menuItems.add(new OptionsItem(null,"Random",0));
        build(menuItems);
    }
    private void verify() {
        String message = "verify account to third party server? session id and user id will get shared. session password and key will not be shared";
        if (Account.isSessionSkyVerified()) {
            message = "you are already verified. you can verify again if you want to";
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("verify")
                .setMessage(message)
                .setCancelable(true)

                .setPositiveButton(
                        "Yes, verify",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                toast("connecting ...");
                                progressBar.setVisibility(View.VISIBLE);
                                getGithubServerInfo();
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
                        Account.setSessionSkyVerified(false);

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
    private void requestAvatar() {
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url;
        if (Account.isLoggedIn()){
            total_url = BASE_URL + "users/"+Account.user_id()+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "users/"+Account.user_id()+"?app=3";
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
                    if (user_avatar == null || user_avatar.equals("")) {
                        toast("no avatar");
                    } else {
                        new DownloadAvatar()
                                .execute("https://avatars.devrant.com/"+user_avatar);
                    }
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

    private void getGithubServerInfo() {
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/skyserver.json";
        MethodsVerifyGithub methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifyGithub.class);
        String header = null;

        Call<ModelVerifyGithub> call = methods.getAllData(header,total_url);
        call.enqueue(new Callback<ModelVerifyGithub>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelVerifyGithub> call, @NonNull Response<ModelVerifyGithub> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    String rant_id = response.body().getRant_id();
                    String notice = response.body().getNotice();
                    Boolean active = response.body().getActive();

                    if (active) {
                        getSkyVerifyKey();
                    } else {
                        toast(notice);
                    }


                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelVerifyGithub> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network github");
                getSkyVerifyKey();
            }
        });

    }

    // Sky server verification
    private void getSkyVerifyKey() {
        try {

        String total_url = SKY_SERVER_URL+"verify_key/"+Account.user_id()+"/"+Account.id();
        MethodsVerifySkyKey methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifySkyKey.class);

        Call<ModelVerifySkyKey> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelVerifySkyKey>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelVerifySkyKey> call, @NonNull Response<ModelVerifySkyKey> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    Boolean success = response.body().getSuccess();
                    Boolean error = response.body().getError();
                    String verify_key = response.body().getVerify_key();
                    String message = response.body().getMessage();
                    String verify_post_id = response.body().getVerify_post_id();

                    if (error || !success) {
                        toast(message);
                    } else {
                        uploadC(verify_post_id,verify_key);
                    }


                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelVerifySkyKey> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                progressBar.setVisibility(View.GONE);
            }
        });
        } catch (Exception e) {
            toast("error connecting to sky. please retry");
            progressBar.setVisibility(View.GONE);
        }
    }
    private void uploadC(String rant_id, String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody comment = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + rant_id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            CommentClient client = retrofit.create(CommentClient.class);
            // finally, execute the request

            Call<com.dev.watchrant.models.ModelSuccess> call = client.upload(app, comment, token_id, token_key, user_id);
            call.enqueue(new Callback<com.dev.watchrant.models.ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<com.dev.watchrant.models.ModelSuccess> call, @NonNull Response<com.dev.watchrant.models.ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            askSkyToVerifyComment();
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
                public void onFailure(@NonNull Call<com.dev.watchrant.models.ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            toast(e.toString());
            progressBar.setVisibility(View.GONE);
        }
    }

    private void askSkyToVerifyComment() {
        try {
        String total_url = SKY_SERVER_URL+"verify_comment/"+Account.user_id()+"/"+Account.id();
        MethodsVerifySkyKey methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifySkyKey.class);

        Call<ModelVerifySkyKey> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelVerifySkyKey>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelVerifySkyKey> call, @NonNull Response<ModelVerifySkyKey> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    Boolean success = response.body().getSuccess();
                    Boolean error = response.body().getError();
                    String message = response.body().getMessage();
                    progressBar.setVisibility(View.GONE);
                    if (error || !success) {
                        toast(message);
                    } else {
                        toast("verified session");
                        Account.setSessionSkyVerified(true);
                        createSkyList();
                    }


                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelVerifySkyKey> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                progressBar.setVisibility(View.GONE);
            }
        });
        } catch (Exception e) {
            toast("error connecting to sky. please retry");
            progressBar.setVisibility(View.GONE);
        }
    }
    private void syncSkyData() {
        getProfile();
    }
    SkyProfile profile;
    public void restore(String restore_size) {
        progressBar.setVisibility(View.GONE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("Sync sky data")
                .setMessage("Sync following, blocked users, blocked words from sky ("+restore_size+")")
                .setCancelable(true)
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();;
                                if (profile!=null) {
                                    if (profile.getFollowing()!=null || profile.getBlocked_words()!=null || profile.getBlocked_users()!=null) {
                                        Account.setFollowing(profile.getFollowing());
                                        Account.setBlockedUsers(profile.getBlocked_users());
                                        Account.setBlockedWords(profile.getBlocked_words());
                                        toast("synced sky data");
                                    } else {
                                        toast("no data to sync");
                                    }
                                }
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
    private void getProfile() {
        progressBar.setVisibility(View.VISIBLE);
        MethodsSkyProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsSkyProfile.class);
        String total_url = SKY_SERVER_URL+"my_profile/"+ Account.user_id()+"/"+Account.id();

        Call<ModelSkyProfile> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelSkyProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSkyProfile> call, @NonNull Response<ModelSkyProfile> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    profile = response.body().getProfile();

                    String restore_data = profile.getFollowing()+profile.getBlocked_users()+profile.getBlocked_words();
                    byte[] restore_b = restore_data.getBytes(StandardCharsets.UTF_8);
                    String restore_size = "restore "+restore_b.length+" bytes";

                    restore(restore_size);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSkyProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}