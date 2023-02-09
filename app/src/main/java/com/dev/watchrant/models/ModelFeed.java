package com.dev.watchrant.models;

import com.dev.watchrant.classes.Rants;
import com.dev.watchrant.classes.Unread;

import java.util.List;

// works for register & login
public class ModelFeed {
    List<Rants> rants;
    Boolean success;
    int num_notifs;
    Unread unread;

    public Unread getUnread() {
        return unread;
    }

    public int getNum_notifs() {
        return num_notifs;
    }

    public Boolean getSuccess() {
        return success;
    }

    public List<Rants> getRants() {
        return rants;
    }


}

