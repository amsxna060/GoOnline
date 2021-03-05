package com.amansiol.goonline.models;

public class Comment {

    String username;
    String timestamp;
    String image;
    String commentbody;

    public Comment() {
    }

    public Comment(String username, String timestamp, String image, String commentbody) {
        this.username = username;
        this.timestamp = timestamp;
        this.image = image;
        this.commentbody = commentbody;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCommentbody() {
        return commentbody;
    }

    public void setCommentbody(String commentbody) {
        this.commentbody = commentbody;
    }
}
