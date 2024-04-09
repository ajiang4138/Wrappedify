package com.example.wrappedify;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WrappedFeed {
    private int profileIcon;
    private String postImage;
    private String title;
    private String message;
    private String timestamp;

    public WrappedFeed() {}

    public WrappedFeed(int profileIcon, String postImage, String title, String message, String timestamp) {
        this.profileIcon = profileIcon;
        this.postImage = postImage;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(int profileIcon) {
        this.profileIcon = profileIcon;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
