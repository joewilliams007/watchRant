package com.dev.watchrant.classes;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class NotifData {
    List<NotifItems> items;
    NotifUnread unread;
    int check_time;


    public int getCheck_time() {
        return check_time;
    }

    public List<NotifItems> getItems() {
        return items;
    }

    public NotifUnread getUnread() {
        return unread;
    }
}
