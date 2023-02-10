package com.dev.watchrant.adapters;

public class RantItem {
    private String text;
    private String image;
    private String type;
    private int score;
    private int numComments;
    private long created_time;

    private int id;
    private String username;
    private int vote_state;

    public int getVote_state() {
        return vote_state;
    }

    public RantItem(String image_url, String text, int id, String type, int score, int numComments, long created_time, String username, int vote_state) {
        this.image = image_url;
        this.text = text;
        this.id = id;
        this.type = type;
        this.score = score;
        this.numComments = numComments;
        this.username = username;
        this.vote_state = vote_state;
    }

    public String getUsername() {
        return username;
    }
    public long getCreated_time() {
        return created_time;
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
