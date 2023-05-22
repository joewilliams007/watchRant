package com.dev.watchrant.methods.sky;


import com.dev.watchrant.models.sky.ModelSkyProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSkyProfile {
    @GET
    Call<ModelSkyProfile> getAllData(@Url String url
    );
}
