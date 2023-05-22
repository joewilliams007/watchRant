package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.toast;
import static com.dev.watchrant.RantActivity.users_of_comments;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;
import static com.dev.watchrant.network.RetrofitClient.SKY_SERVER_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.databinding.ActivityReplyBinding;
import com.dev.watchrant.methods.sky.MethodsVerifySkyKey;
import com.dev.watchrant.models.ModelLogin;
import com.dev.watchrant.models.ModelSuccess;
import com.dev.watchrant.models.sky.ModelVerifySkyKey;
import com.dev.watchrant.network.RetrofitClient;
import com.dev.watchrant.post.CommentClient;
import com.dev.watchrant.post.LoginClient;
import com.fasterxml.jackson.databind.node.BooleanNode;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;

public class ReplyActivity extends Activity {

    private ActivityReplyBinding binding;
    EditText editText;
    TextView textViewSubmit;
    String id;
    public static String replyText = "";
    public static Boolean uploaded_comment = false;
    public static Boolean isReaction = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText = findViewById(R.id.editText);
        textViewSubmit = findViewById(R.id.textViewSubmit);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        uploaded_comment = false;


        if (isReaction) {
            textViewSubmit.setText("submit emoji");
            editText.setHint("reaction (emoji)");
        } else {
            textViewSubmit.setText("submit comment");
            editText.setText(replyText);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        replyText = editText.getText().toString();
    }

    public void enter(View view) {
        String c = editText.getText().toString();
        if (isReaction) {
            react(c);
        } else {
            if (c.length()<1) {
                toast("enter comment");
            } else {
                toast("uploading ...");
                uploadC(c);
            }
        }

    }

    private void react(String c) {

        if (c.length()<1) {
                toast("enter emoji");
                return;
            }

            if (c.length()>3) {
                toast("enter one emoji only");
                return;
            }

            if (c.contains("u")) {
                if (c.replaceFirst("u","").contains("u")) {
                    toast("enter only one emoji");
                    return;
                }
            }



            String total_url = SKY_SERVER_URL+"react_post/"+Account.user_id()+"/"+Account.id()+"/"+id+"/"+ c;
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

                        if (success) {
                            uploaded_comment = true;
                            finish();
                        }

                        if (error) {
                            toast(message);
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
                }
            });
    }


    private void uploadC(String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody comment = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            CommentClient client = retrofit.create(CommentClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(app, comment, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            toast("success!");
                        } else {
                            toast("failed");
                        }
                        uploaded_comment = true;
                        finish();
                        replyText = "";
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
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                }

            });
        } catch (Exception e) {
            editText.setText(e.toString());
        }
    }
}