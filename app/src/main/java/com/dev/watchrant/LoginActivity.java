package com.dev.watchrant;

import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.databinding.ActivityLoginBinding;
import com.dev.watchrant.methods.MethodsFeed;
import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.models.ModelLogin;
import com.dev.watchrant.network.RetrofitClient;
import com.dev.watchrant.post.LoginClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {

    EditText editText;
    TextView textView;
    int tapped = 0;
    String _username;
    String _password;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textViewSubmit);
        toast("enter username");
    }

    public void toast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void enter(View view) {
        String text = editText.getText().toString();
        if (text.length() < 1) {
            if (tapped == 0) {
                toast("enter username");
            } else {
                editText.setText("");
                toast("enter password");
            }
            return;
        } else {
            if (tapped == 1) {
                _password = text;
                editText.setText("");
                login();
            } else {
                tapped++;
                editText.setText("");
                editText.setHint("password");
                textView.setText("submit password");
                _username = text;
            }
        
        }
    }

    private void login() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
        RequestBody username = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _username);
        RequestBody password = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _password);

        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL) // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        LoginClient client = retrofit.create(LoginClient.class);
        // finally, execute the request

        Call<ModelLogin> call = client.login(app,username,password);
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    String key = response.body().getAuth_token().getKey();
                    int id = response.body().getAuth_token().getId();
                    int user_id = response.body().getAuth_token().getUser_id();
                    int expire_time = response.body().getAuth_token().getExpire_time();

                    Account.setKey(key);
                    Account.setId(id);
                    Account.setUser_id(user_id);
                    Account.setExpire_time(expire_time);

                    tryTokens();

                } else if (response.code() == 400) {
                    toast("Invalid login credentials entered. Please try again. :(");
                    editText.setHint("username");
                    editText.setText("");
                    textView.setText("submit username");
                    progressBar.setVisibility(View.GONE);
                    tapped = 0;
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("You are not authorized :P");
                } else {
                    toast(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
            }

        });
    }



    private void tryTokens() {
            MethodsFeed methods = RetrofitClient.getRetrofitInstance().create(MethodsFeed.class);
            String  total_url = BASE_URL + "devrant/rants?token_id="+Account.id()+"&user_id="+Account.user_id()+"&token_key="+Account.key()+"&limit=10&sort=recent&app=3&range=day&skip=0/";
            Call<ModelFeed> call = methods.getAllData(total_url);
            call.enqueue(new Callback<ModelFeed>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelFeed> call, @NonNull Response<ModelFeed> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                        toast("login approved");
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("you are not authorized");
                    } else {
                        login();
                    }

            }

            @Override
            public void onFailure(@NonNull Call<ModelFeed> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                    toast("no network");
            }});
    }
}