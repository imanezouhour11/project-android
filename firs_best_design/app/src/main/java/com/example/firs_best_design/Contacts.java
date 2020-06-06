package com.example.firs_best_design;

public class Contacts {
    public String name, status, image, state;

    public String getState() {
        return state;
    }

    public Contacts() {

    }

    public void setState(String state) {
        this.state = state;
    }

    public Contacts(String name, String status, String image, String state) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}