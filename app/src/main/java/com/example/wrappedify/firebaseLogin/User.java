package com.example.wrappedify.firebaseLogin;

import com.example.wrappedify.WrappedFeed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class User {
    public static final String CLIENT_ID = "ccb7c7bbeb9d455e96a4fbaac95885f1";
    public static final String REDIRECT_URI = "wrappedify://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static String accessToken;
    public static ArrayList<String> genres;
    public static ArrayList<String> artistId;
    public static ArrayList<String> trackId;
    public static String profileImage;

    public static String displayName;
    public static String generatedTerm;

    public static boolean privateAcc = false;

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

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static String getGeneratedTerm() {
        return generatedTerm;
    }

    public static String getProfileImage() {
        return profileImage;
    }

    public static void setProfileImage(String profileImage) {
        User.profileImage = profileImage;
    }

    public static String getDisplayName() {
        return displayName;
    }

    public static void setDisplayName(String displayName) {
        User.displayName = displayName;
    }

    public static void setGeneratedTerm(String generatedTerm) {
        User.generatedTerm = generatedTerm;
    }

    public static boolean isPrivateAcc() {
        return privateAcc;
    }

    public static void setPrivateAcc(boolean privateAcc) {
        User.privateAcc = privateAcc;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(User.currentUserId());
    }
}
