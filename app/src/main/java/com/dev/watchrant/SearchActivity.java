package com.dev.watchrant;

import static com.dev.watchrant.network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dev.watchrant.animations.Tools;
import com.dev.watchrant.auth.Account;
import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.databinding.ActivitySearchBinding;
import com.dev.watchrant.methods.MethodsSearch;
import com.dev.watchrant.models.ModelSearch;
import com.dev.watchrant.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends Activity {

    private TextView mTextView;
    private ActivitySearchBinding binding;
    public static List<Rants> search_rants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // mTextView = binding.text;
    }

    public void search(View view) {
        EditText editText = findViewById(R.id.editTextSearch);
        String search = editText.getText().toString();
        if (search.length()>0) {
            requestFeed(search);
        }
    }

    private void requestFeed(String term) {
        MethodsSearch methods = RetrofitClient.getRetrofitInstance().create(MethodsSearch.class);
        String total_url = BASE_URL + "devrant/search?app=3&term="+term;

        Call<ModelSearch> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelSearch>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSearch> call, @NonNull Response<ModelSearch> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<Rants> rants = response.body().getRants();
                    //   toast("success: "+success+" size: "+rants.size());

                    search_rants = rants;

                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    intent.putExtra("isSearch",true);
                    startActivity(intent);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {

                }


            }

            @Override
            public void onFailure(@NonNull Call<ModelSearch> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void toast(String message) {
        Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}