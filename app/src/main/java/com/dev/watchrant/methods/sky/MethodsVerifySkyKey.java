package com.dev.watchrant.methods.sky;

import com.dev.watchrant.models.sky.ModelVerifySkyKey;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsVerifySkyKey {
    @GET
    Call<ModelVerifySkyKey> getAllData(@Url String url
    );
}
