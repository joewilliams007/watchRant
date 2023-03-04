package com.dev.watchrant;

import static com.dev.watchrant.RantActivity.toast;
import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.databinding.ActivityWriteRantBinding;
import com.dev.watchrant.models.ModelSuccess;
import com.dev.watchrant.post.CommentClient;
import com.dev.watchrant.post.RantClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteRantActivity extends Activity {

    private ActivityWriteRantBinding binding;
    EditText editText;
    String _type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityWriteRantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        _type = intent.getStringExtra("type");

        editText = findViewById(R.id.editText);
    }

    public void enter(View view) {
        String c = editText.getText().toString();
        if (c.length()<7) {
            toast("enter rant");
        } else {
            toast("uploading ...");
            uploadC(c);
        }
    }

    private void uploadC(String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody rant = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody type = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _type);
            RequestBody tags = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "");
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            RantClient client = retrofit.create(RantClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(app, rant, type, tags, token_id, token_key, user_id);
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
                            finish();
                            editText.setText("");

                            Intent intent = new Intent(WriteRantActivity.this, RantActivity.class);
                            intent.putExtra("id",String.valueOf(response.body().getRant_id())); // open the posted rant
                            startActivity(intent);
                        } else {
                            toast("failed. please wait until you upload a new rant");
                            // editText.setText(response.message()+" "+response.toString());
                        }
                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                        editText.setText(response.message());
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