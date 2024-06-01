package com.example.projectakhir.Model;

public class Comment {

    private String commentId;
    private String userId;
    private String userName;
    private String commentText;
    long  timestamp;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    private double rating;

    public Comment() {
        //konstruktor kosong untuk Firebase
    }

    public Comment(String userId, String userName, String commentText,long time,double rating) {
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
        this.timestamp= time;
        this.rating = rating;

    }

    //getter dan setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getCommentId() {
        return commentId;
    }
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
