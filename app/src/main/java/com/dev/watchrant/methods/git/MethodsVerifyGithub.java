package com.dev.watchrant.methods.git;


import com.dev.watchrant.models.git.ModelVerifyGithub;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface MethodsVerifyGithub {
    @GET
    Call<ModelVerifyGithub> getAllData(@Header("Authorization") String auth,
                                       @Url String url
    );
}
