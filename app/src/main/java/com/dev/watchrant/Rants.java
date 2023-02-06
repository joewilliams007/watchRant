package com.dev.watchrant;

public class Rants {
    int id;
    int score;
    String text;
    int created_time;
    int num_comments;
    Boolean edited;
    String user_username;
    String link;

    public String getLink() {
        return link;
    }

    int user_score;
    int user_id;
    User_avatar user_avatar;

    public User_avatar getUser_avatar() {
        return user_avatar;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getText() {
        return text;
    }

    public int getCreated_time() {
        return created_time;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public Boolean getEdited() {
        return edited;
    }

    public String getUser_username() {
        return user_username;
    }

    public int getUser_score() {
        return user_score;
    }

    public int getUser_id() {
        return user_id;
    }
}
