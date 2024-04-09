package com.example.wrappedify;

public class WrappedFeed {
    private int profileIcon;
    private String postImage;
    private String title;
    private String message;

    public WrappedFeed() {}

    public WrappedFeed(int profileIcon, String postImage, String title, String message) {
        this.profileIcon = profileIcon;
        this.postImage = postImage;
        this.title = title;
        this.message = message;
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
}
