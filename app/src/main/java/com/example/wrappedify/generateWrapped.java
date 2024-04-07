package com.example.wrappedify;

import static com.example.wrappedify.imageDrawer.imageDrawer.textAsBitmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wrappedify.firebaseLogin.User;
import com.example.wrappedify.imageDrawer.imageDrawer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class generateWrapped extends AppCompatActivity {
    public static final String CLIENT_ID = "ccb7c7bbeb9d455e96a4fbaac95885f1";
    public static final String CLIENT_SECRET = "6daf73f2aea74601bd4925a1d1430294";
    public static final String REDIRECT_URI = "wrappedify://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    // Initialize views
    private ConstraintLayout rootContent;
    private TextView textViewSong1, textViewSong2, textViewSong3, textViewGenres, textViewAi, textViewRecommendation;
    private TextView textViewArtist1, textViewArtist2, textViewArtist3, topFiveArtists;
    private ImageView imageView1, imageView2, imageView3, artistView, generateImageView;

    // Initialize buttons
    private Button backBtn, saveBtn;
    private FloatingActionButton menuFab, shortTermFab, mediumTermFab, longTermFab;

    boolean menuOpen = false;
    float translationYaxis = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    FirebaseAuth mAuth;
    FirebaseUser user;
    StorageReference storageRef;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);

        // Set views
        rootContent = findViewById(R.id.root_content2);

        textViewSong1 = findViewById(R.id.textViewSongOne);
        textViewSong2 = findViewById(R.id.textViewSongTwo);
        textViewSong3 = findViewById(R.id.textViewSongThree);

        textViewArtist1 = findViewById(R.id.textViewArtistOne);
        textViewArtist2 = findViewById(R.id.textViewArtistTwo);
        textViewArtist3 = findViewById(R.id.textViewArtistThree);

        topFiveArtists = findViewById(R.id.topFiveArtists);

        textViewGenres = findViewById(R.id.TextViewGenres);

        textViewAi = findViewById(R.id.TextViewAI);
        textViewRecommendation = findViewById(R.id.TextViewRecommendations);

        imageView1 = findViewById(R.id.imageViewSongOne);
        imageView2 = findViewById(R.id.imageViewSongTwo);
        imageView3 = findViewById(R.id.imageViewSongThree);
        artistView = findViewById(R.id.artistImage);

        generateImageView = findViewById(R.id.generateImageView);

        // Set buttons
        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveBtn);

        menuFab = findViewById(R.id.menuFab);
        shortTermFab = findViewById(R.id.short_term_fab);
        shortTermFab.setImageBitmap(textAsBitmap("Short", 40, Color.WHITE));
        mediumTermFab = findViewById(R.id.medium_term_fab);
        mediumTermFab.setImageBitmap(textAsBitmap("Med", 40, Color.WHITE));
        longTermFab = findViewById(R.id.long_term_fab);
        longTermFab.setImageBitmap(textAsBitmap("Long", 40, Color.WHITE));

        // Set FireBase authentication
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        showMenu();

        backBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), dashboard.class);
            startActivity(intent);
            finish();
        });

        saveBtn.setOnClickListener((v) -> {
            goSave();
            Toast.makeText(generateWrapped.this, "Saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), dashboard.class);
            startActivity(intent);
            finish();
        });


        shortTermFab.setOnClickListener((v) -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getShortTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getShortTopTracks();
        });

        mediumTermFab.setOnClickListener((v) -> {
            getMediumTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getMediumTopTracks();
        });

        longTermFab.setOnClickListener((v) -> {
            getLongTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getLongTopTracks();
        });
    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(generateWrapped.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            User.setAccessToken(response.getAccessToken());
        }
    }

    /**
     * Get top 5 recently listened to artists, by short term.
     */
    public void getShortTopArtist() {
        if (User.accessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject artistInfo = jsonItems.getJSONObject(i);
                        String name = artistInfo.getString("name");

                        JSONArray genreList = artistInfo.getJSONArray("genres");
                        String[] genre = new String[genreList.length()];
                        for (int j = 0; j < genreList.length(); j++) {
                            genre[j] = genreList.getString(j);
                        }

                        names.add(name);
                        genres.addAll(Arrays.asList(genre));
                    }

                    // Setting Image
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistView);
                        }
                    });

                    output = informationFetcher.genresText(informationFetcher.top3Genre(genres));

                    setTextAsync(output, textViewGenres);

                    output = informationFetcher.namesText(names);

                    setTextAsync(output, topFiveArtists);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 3 recently listened to tracks, by short term
     */
    public void getShortTopTracks() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=3&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    ArrayList<String> artistNames = new ArrayList<>();
                    ArrayList<String> trackId = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");

                        JSONArray albumArt = albumInfo.getJSONArray("images");
                        JSONObject albumImage = albumArt.getJSONObject(0);
                        String imageURL = albumImage.getString("url");

                        for (int j = 0; j < artistInfo.length(); j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");
                        trackId.add(trackInfo.getString("id"));
                        String artists = informationFetcher.artistText(artistNames);

                        if (i == 0) {
                            setTextAsync(name + "\n" + artists, textViewSong1);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(name + "\n" + artists, textViewSong2);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView2);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(name + "\n" + artists, textViewSong3);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView3);
                                }
                            });
                        }
                    }

                    User.setTrackId(trackId);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get recommendations based on short term activity
     */
    public void getShortRecommendations() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=3&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");

                        JSONArray albumArt = albumInfo.getJSONArray("images");
                        JSONObject albumImage = albumArt.getJSONObject(0);
                        String imageURL = albumImage.getString("url");

                        for (int j = 0; j < artistInfo.length(); j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");
                        String artists = informationFetcher.artistText(artistNames);

                        if (i == 0) {
                            setTextAsync(name + "\n" + artists, textViewSong1);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(name + "\n" + artists, textViewSong2);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView2);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(name + "\n" + artists, textViewSong3);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView3);
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 5 recently listened to artists, by medium term.
     */
    public void getMediumTopArtist() {
        if (User.accessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=medium_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject artistInfo = jsonItems.getJSONObject(i);
                        String name = artistInfo.getString("name");

                        JSONArray genreList = artistInfo.getJSONArray("genres");
                        String[] genre = new String[genreList.length()];
                        for (int j = 0; j < genreList.length(); j++) {
                            genre[j] = genreList.getString(j);
                        }

                        names.add(name);
                        genres.addAll(Arrays.asList(genre));
                    }

                    // Setting Image
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistView);
                        }
                    });

                    output = informationFetcher.genresText(informationFetcher.top3Genre(genres));

                    setTextAsync(output, textViewGenres);

                    output = informationFetcher.namesText(names);

                    setTextAsync(output, topFiveArtists);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 3 recently listened to tracks, by medium term
     */
    public void getMediumTopTracks() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=3&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");

                        JSONArray albumArt = albumInfo.getJSONArray("images");
                        JSONObject albumImage = albumArt.getJSONObject(0);
                        String imageURL = albumImage.getString("url");

                        for (int j = 0; j < artistInfo.length(); j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");
                        String artists = informationFetcher.artistText(artistNames);

                        if (i == 0) {
                            setTextAsync(name + "\n" + artists, textViewSong1);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(name + "\n" + artists, textViewSong2);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView2);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(name + "\n" + artists, textViewSong3);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView3);
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 5 recently listened to artists, by long term.
     */
    public void getLongTopArtist() {
        if (User.accessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject artistInfo = jsonItems.getJSONObject(i);
                        String name = artistInfo.getString("name");

                        JSONArray genreList = artistInfo.getJSONArray("genres");
                        String[] genre = new String[genreList.length()];
                        for (int j = 0; j < genreList.length(); j++) {
                            genre[j] = genreList.getString(j);
                        }

                        names.add(name);
                        genres.addAll(Arrays.asList(genre));
                    }

                    // Setting Image
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistView);
                        }
                    });

                    output = informationFetcher.genresText(informationFetcher.top3Genre(genres));

                    setTextAsync(output, textViewGenres);

                    output = informationFetcher.namesText(names);

                    setTextAsync(output, topFiveArtists);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 3 recently listened to tracks, by long term
     */
    public void getLongTopTracks() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=3&offset=0")
                .addHeader("Authorization", "Bearer " + User.getAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(generateWrapped.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");

                        JSONArray albumArt = albumInfo.getJSONArray("images");
                        JSONObject albumImage = albumArt.getJSONObject(0);
                        String imageURL = albumImage.getString("url");

                        for (int j = 0; j < artistInfo.length(); j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");
                        String artists = informationFetcher.artistText(artistNames);

                        if (i == 0) {
                            setTextAsync(name + "\n" + artists, textViewSong1);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(name + "\n" + artists, textViewSong2);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView2);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(name + "\n" + artists, textViewSong3);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView3);
                                }
                            });
                        }
                    }

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(generateWrapped.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Create a drawable from URL
     */
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private void goSave() {
        Bitmap image = imageDrawer.getBitmapFromView(rootContent);
        generateImageView.setImageBitmap(image);
        imageUri = getImageUri(generateWrapped.this, image);
        saveImage(image);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File (myDir, fname);
        // if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            uploadImage();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    private void uploadImage() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy__MM__dd_HH_mm_ss", Locale.ENGLISH);
        Date date = new Date();
        String fileName = formatter.format(date);

        storageRef = FirebaseStorage.getInstance().getReference(user.getEmail() + "/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(generateWrapped.this, "Uploaded to fb", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(generateWrapped.this, "Failed to fb", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Custom FloatingActionButton setup
    private void showMenu() {

        menuFab = findViewById(R.id.menuFab);
        shortTermFab = findViewById(R.id.short_term_fab);
        mediumTermFab = findViewById(R.id.medium_term_fab);
        longTermFab = findViewById(R.id.long_term_fab);

        shortTermFab.setAlpha(0f);
        mediumTermFab.setAlpha(0f);
        longTermFab.setAlpha(0f);

        shortTermFab.setTranslationY(translationYaxis);
        mediumTermFab.setTranslationY(translationYaxis);
        longTermFab.setTranslationY(translationYaxis);

        menuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuOpen) {
                    closeMenu();
                }

                else {
                    openMenu();
                }
            }
        });
    }

    private void openMenu() {
        menuOpen = !menuOpen;
        shortTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        mediumTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        longTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu() {
        menuOpen = !menuOpen;
        shortTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        mediumTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        longTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}
