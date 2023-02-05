package com.dev.watchrant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dev.watchrant.databinding.ActivityLoginBinding;
import com.dev.watchrant.methods.MethodsLogin;
import com.dev.watchrant.methods.MethodsUpdate;
import com.dev.watchrant.models.ModelLogin;
import com.dev.watchrant.models.ModelUpdate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    EditText editText;
    TextView textView;
    int tapped = 0;
    String username;
    String password;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                toast("enter password");
            }
            return;
        } else {
            if (tapped == 1) {
                password = text;
                login();
            } else {
                tapped++;
                editText.setHint("password");
                textView.setText("submit password");
                username = text;
            }
        
        }
    }

    private void login() {
        MethodsLogin methods = RetrofitClient.getRetrofitInstance().create(MethodsLogin.class);
        String total_url = "users/auth-token?app=3&username="+username+"&password="+password;

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

    }

}