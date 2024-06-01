package com.example.projectakhir.Model;

public class Rating {
    private String userId;
    private double rating;

    private String destId;

    public Rating() {
    }

    public Rating(String userId,String destId,double rating) {
        this.userId = userId;
        this.destId = destId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }
}
