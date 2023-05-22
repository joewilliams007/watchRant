package com.dev.watchrant.models.sky;

import com.dev.watchrant.classes.sky.SkyProfile;

// works for register & login
public class ModelSkyProfile {
    Boolean success;
    Boolean error;
    String message;
    SkyProfile profile;

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public SkyProfile getProfile() {
        return profile;
    }
}