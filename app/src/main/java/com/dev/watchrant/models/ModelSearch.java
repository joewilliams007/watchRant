package com.dev.watchrant.models;

import com.dev.watchrant.classes.Rants;

import java.util.List;

// works for register & login
public class ModelSearch {
    List<Rants> results;
    Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public List<Rants> getRants() {
        return results;
    }


}

