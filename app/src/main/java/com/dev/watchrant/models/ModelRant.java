package com.dev.watchrant.models;

import com.dev.watchrant.Comment;
import com.dev.watchrant.Rants;

import java.util.List;

// works for register & login
public class ModelRant {
    List<Comment> comments;
    Rants rant;

    public List<Comment> getComments() {
        return comments;
    }

    public Rants getRant() {
        return rant;
    }
}

