package com.dev.watchrant.adapters;

public class MenuItem {
    private String text;
    private String image;

    private int id;

    public MenuItem(String image_url, String text, int id) {
        this.image = image_url;
        this.text = text;
        this.id = id;
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

}
