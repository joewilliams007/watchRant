package com.dev.watchrant.classes;

public class Comment {
    int id;
    int rant_id;
    String body;
    int score;
    int created_time;
    int vote_status;
    int user_id;
    String user_username;
    int user_score;

    public int getId() {
        return id;
    }

    public int getRant_id() {
        return rant_id;
    }

    public String getBody() {
        return body;
    }

    public int getScore() {
        return score;
    }

    public int getCreated_time() {
        return created_time;
    }

    public int getVote_status() {
        return vote_status;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_username() {
        return user_username;
    }

    public int getUser_score() {
        return user_score;
    }
}
