package com.dev.watchrant.methods;

import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.models.ModelProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsProfile {
    @GET
    Call<ModelProfile> getAllData(
            @Url String url
    );
}
