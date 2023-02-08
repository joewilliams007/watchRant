package com.dev.watchrant.post;

import com.dev.watchrant.models.ModelLogin;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LoginClient {
    @Multipart

    @POST("users/auth-token")
    Call<ModelLogin> login(
            @Part("app") RequestBody app,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password
    );
}