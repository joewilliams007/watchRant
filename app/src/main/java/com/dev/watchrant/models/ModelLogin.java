package com.dev.watchrant.models;

import com.dev.watchrant.Auth_token;
import com.dev.watchrant.Rants;

import java.util.List;

// works for register & login
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

