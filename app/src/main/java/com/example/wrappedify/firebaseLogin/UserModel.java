package com.example.wrappedify.firebaseLogin;

import com.google.firebase.Timestamp;
public class UserModel {
    private Timestamp createdTimestamp;
    private String userId;

    public UserModel() {

    }

    public UserModel(Timestamp createdTimestamp, String userId) {
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
