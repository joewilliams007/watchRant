package com.dev.watchrant.models;

import com.dev.watchrant.classes.Auth_token;


public class ModelLogin {
    Boolean success;
    Auth_token auth_token;

    public Boolean getSuccess() {
        return success;
    }

    public Auth_token getAuth_token() {
        return auth_token;
    }
}

