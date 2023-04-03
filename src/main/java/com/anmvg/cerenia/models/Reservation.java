package com.anmvg.cerenia.models;

import java.util.Date;

public class Reservation {

    private Integer id;
    private User user;
    private Trip trip;
    private Integer state;
    private Integer nbPeople;
    private Date createdAt;

    public Reservation(Integer id, User user, Trip trip, Integer state, Integer nbPeople, Date createdAt) {
        this.id = id;
        this.user = user;
        this.trip = trip;
        this.state = state;
        this.nbPeople = nbPeople;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getNbPeople() {
        return nbPeople;
    }

    public void setNbPeople(Integer nbPeople) {
        this.nbPeople = nbPeople;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
