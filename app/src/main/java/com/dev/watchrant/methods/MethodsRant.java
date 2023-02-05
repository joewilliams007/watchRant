package com.dev.watchrant.methods;

import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.models.ModelRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsRant {
    @GET
    Call<ModelRant> getAllData(
            @Url String url
    );
}
