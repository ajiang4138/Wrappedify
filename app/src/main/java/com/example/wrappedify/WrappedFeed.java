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

    public String getPostImage() {
        return postImage;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

}
