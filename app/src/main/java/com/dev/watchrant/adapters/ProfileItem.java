package com.dev.watchrant.adapters;

public class ProfileItem {
    private String text;
    private String image;
    private String type;
    private int score;
    private int numComments;

    private int id;

    public ProfileItem(String image_url, String text, int id, String type, int score, int numComments) {
        this.image = image_url;
        this.text = text;
        this.id = id;
        this.type = type;
        this.score = score;
        this.numComments = numComments;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getNumComments() {
        return numComments;
    }

}
