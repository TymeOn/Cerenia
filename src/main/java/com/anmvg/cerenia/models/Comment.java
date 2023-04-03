package com.anmvg.cerenia.models;

public class Comment {

    private User user;
    private Integer rating;
    private String text;

    public Comment(User user, Integer rating, String text) {
        this.user = user;
        this.rating = rating;
        this.text = text;
        // TODO : adding a "created at" field
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
