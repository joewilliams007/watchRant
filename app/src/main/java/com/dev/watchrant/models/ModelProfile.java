package com.dev.watchrant.models;

import com.dev.watchrant.Profile;
import com.dev.watchrant.Rants;

import java.util.List;

// works for register & login
public class ModelProfile {
    Profile profile;
    Boolean success;

    public Profile getProfile() {
        return profile;
    }

    public Boolean getSuccess() {
        return success;
    }
}

