package com.example.self;

import com.google.firebase.Timestamp;

public class Journal {

    private String userName;
    private String UserId;
    private String title;
    private String thought;
    private Timestamp timeadded;

    public Journal(){

    }

    public Journal(String userName, String userId, String title, String thought, Timestamp timeadded) {
        this.userName = userName;
        this.UserId = userId;
        this.title = title;
        this.thought = thought;
        this.timeadded = timeadded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public Timestamp getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(Timestamp timeadded) {
        this.timeadded = timeadded;
    }
}

