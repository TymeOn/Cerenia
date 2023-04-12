package com.anmvg.cerenia.models;

import java.util.Date;

public class Comment {

    private User user;
    private Integer rating;
    private String text;
    private Date createdAt;

    public Comment(User user, Integer rating, String text, Date createdAt) {
        this.user = user;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
