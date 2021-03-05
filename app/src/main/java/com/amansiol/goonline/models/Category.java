package com.amansiol.goonline.models;

public class Category {

    String title;
    int bgimage;

    public Category(String title, int bgimage) {
        this.title = title;
        this.bgimage = bgimage;
    }

    public Category() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBgimage() {
        return bgimage;
    }

    public void setBgimage(int bgimage) {
        this.bgimage = bgimage;
    }
}
