package com.dev.watchrant.methods;

import com.dev.watchrant.models.ModelImgRant;
import com.dev.watchrant.models.ModelRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsImgRant {
    @GET
    Call<ModelImgRant> getAllData(
            @Url String url
    );
}
