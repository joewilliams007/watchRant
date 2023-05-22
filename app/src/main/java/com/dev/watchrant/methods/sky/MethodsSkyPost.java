package com.dev.watchrant.methods.sky;

import com.dev.watchrant.models.sky.ModelSkyPost;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSkyPost {
    @GET
    Call<ModelSkyPost> getAllData(@Url String url
    );
}
