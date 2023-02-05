package com.dev.watchrant.models;

import com.dev.watchrant.Comment;
import com.dev.watchrant.ImgRant;
import com.dev.watchrant.Rants;

import java.util.List;

// works for register & login
public class ModelImgRant {
    List<Comment> comments;
    ImgRant rant;

    public List<Comment> getComments() {
        return comments;
    }

    public ImgRant getImgRant() {
        return rant;
    }
}

