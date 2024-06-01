package com.example.projectakhir;

public class Review {
    private String name;
    private String time;
    private String reviewText;
    private float rating;

    public Review(String name, String time, String reviewText, float rating) {
        this.name = name;
        this.time = time;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getReviewText() {
        return reviewText;
    }

    public float getRating() {
        return rating;
    }
}