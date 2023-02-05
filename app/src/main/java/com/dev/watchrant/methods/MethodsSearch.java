package com.dev.watchrant.methods;

import com.dev.watchrant.models.ModelFeed;
import com.dev.watchrant.models.ModelSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSearch {
    @GET
    Call<ModelSearch> getAllData(
            @Url String url
    );
}
