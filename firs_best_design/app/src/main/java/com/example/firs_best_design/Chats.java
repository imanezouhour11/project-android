package com.example.firs_best_design;

public class Chats {
    private String username,date,status;
    private String userPhoto;


    public Chats(String username, String date, String status, String userPhoto, String deletePhoto) {
        this.username = username;
        this.date = date;
        this.status = status;
        this.userPhoto = userPhoto;

    }

    public Chats() {
    }


    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getStatus() {
        return status;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Chats(String username, String date, String status, String userPhoto) {
        this.username = username;
        this.date = date;
        this.status = status;
        this.userPhoto = userPhoto;
    }
}
