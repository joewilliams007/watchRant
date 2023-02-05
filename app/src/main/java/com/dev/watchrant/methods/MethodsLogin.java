package com.dev.watchrant.methods;

import com.dev.watchrant.models.ModelLogin;
import com.dev.watchrant.models.ModelSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsLogin {
    @GET
    Call<ModelLogin> getAllData(
            @Url String url
    );
}
