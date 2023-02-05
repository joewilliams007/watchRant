package com.dev.watchrant.models;

import com.dev.watchrant.Rants;

import java.util.List;

// works for register & login
public class ModelFeed {
    List<Rants> rants;
    Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public List<Rants> getRants() {
        return rants;
    }


}

