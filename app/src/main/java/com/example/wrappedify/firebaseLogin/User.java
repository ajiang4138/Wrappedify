package com.example.wrappedify.firebaseLogin;

import java.util.ArrayList;

public class User {
    public static String accessToken;
    public static ArrayList<String> genres;
    public static ArrayList<String> artistId;
    public static ArrayList<String> trackId;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        User.accessToken = accessToken;
    }

    public static ArrayList<String> getGenres() {
        return genres;
    }

    public static void setGenres(ArrayList<String> genres) {
        User.genres = genres;
    }

    public static ArrayList<String> getArtistId() {
        return artistId;
    }

    public static void setArtistId(ArrayList<String> artistId) {
        User.artistId = artistId;
    }

    public static ArrayList<String> getTrackId() {
        return trackId;
    }

    public static void setTrackId(ArrayList<String> trackId) {
        User.trackId = trackId;
    }
}
