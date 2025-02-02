package com.example.wrappedify;

import static com.example.wrappedify.imageDrawer.imageDrawer.textAsBitmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.wrappedify.firebaseLogin.User;
import com.example.wrappedify.imageDrawer.imageDrawer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

    // Saving Image
    private ConstraintLayout rootContent;

    // Top Track views
    private TextView textViewSong1, textViewSong2, textViewSong3, textViewSong4, textViewSong5;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, generateImageView;

    // Top Artist views
    private TextView topArtist1, topArtist2, topArtist3, topArtist4, topArtist5, topArtist6;
    private ImageView artistImage1, artistImage2, artistImage3, artistImage4, artistImage5, artistImage6;

    // Recommendations
    private TextView textViewRecommendation, recText1, recText2, recText3, recText4, recText5, recText6;
    private ImageView recImage1, recImage2, recImage3, recImage4, recImage5, recImage6;

    // Top Genre Views
    private TextView genre1, genre2, genre3;

    // Initialize buttons
    private FloatingActionButton menuFab, shortTermFab, mediumTermFab, longTermFab;
    private FloatingActionButton themesFab, defaultFab, christmasFab, halloweenFab, newyearsFab, stpatFab, valentinesFab;

    boolean menuOpen = false;
    boolean themeOpen = false;
    float translationYaxis = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageRef;
    Uri imageUri;
    private String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);

        // Set views
        ConstraintLayout backgroundContent = findViewById(R.id.root_content);
        rootContent = findViewById(R.id.root_content2);

        textViewSong1 = findViewById(R.id.textViewSongOne);
        textViewSong2 = findViewById(R.id.textViewSongTwo);
        textViewSong3 = findViewById(R.id.textViewSongThree);
        textViewSong4 = findViewById(R.id.textViewSongFour);
        textViewSong5 = findViewById(R.id.textViewSongFive);

        topArtist1 = findViewById(R.id.artistTextOne);
        topArtist2 = findViewById(R.id.artistTextTwo);
        topArtist3 = findViewById(R.id.artistTextThree);
        topArtist4 = findViewById(R.id.artistTextFour);
        topArtist5 = findViewById(R.id.artistTextFive);
        topArtist6 = findViewById(R.id.artistTextSix);

        recText1 = findViewById(R.id.recommendationTextOne);
        recText2 = findViewById(R.id.recommendationTextTwo);
        recText3 = findViewById(R.id.recommendationTextThree);
        recText4 = findViewById(R.id.recommendationTextFour);
        recText5 = findViewById(R.id.recommendationTextFive);
        recText6 = findViewById(R.id.recommendationTextSix);

        genre1 = findViewById(R.id.genreTextOne);
        genre2 = findViewById(R.id.genreTextTwo);
        genre3 = findViewById(R.id.genreTextThree);

        textViewRecommendation = findViewById(R.id.recommendations);

        imageView1 = findViewById(R.id.imageViewSongOne);
        imageView2 = findViewById(R.id.imageViewSongTwo);
        imageView3 = findViewById(R.id.imageViewSongThree);
        imageView4 = findViewById(R.id.imageViewSongFour);
        imageView5 = findViewById(R.id.imageViewSongFive);

        artistImage1 = findViewById(R.id.artistImageOne);
        artistImage2 = findViewById(R.id.artistImageTwo);
        artistImage3 = findViewById(R.id.artistImageThree);
        artistImage4 = findViewById(R.id.artistImageFour);
        artistImage5 = findViewById(R.id.artistImageFive);
        artistImage6 = findViewById(R.id.artistImageSix);

        recImage1 = findViewById(R.id.recommendationImageOne);
        recImage2 = findViewById(R.id.recommendationImageTwo);
        recImage3 = findViewById(R.id.recommendationImageThree);
        recImage4 = findViewById(R.id.recommendationImageFour);
        recImage5 = findViewById(R.id.recommendationImageFive);
        recImage6 = findViewById(R.id.recommendationImageSix);

        generateImageView = findViewById(R.id.generateImageView);

        // Set buttons
        Button backBtn = findViewById(R.id.backBtn);
        Button saveBtn = findViewById(R.id.saveBtn);

        menuFab = findViewById(R.id.menuFab);

        shortTermFab = findViewById(R.id.short_term_fab);
        shortTermFab.setImageBitmap(textAsBitmap("Week", 40, Color.WHITE));
        mediumTermFab = findViewById(R.id.medium_term_fab);
        mediumTermFab.setImageBitmap(textAsBitmap("Month", 40, Color.WHITE));
        longTermFab = findViewById(R.id.long_term_fab);
        longTermFab.setImageBitmap(textAsBitmap("Year", 40, Color.WHITE));

        themesFab = findViewById(R.id.themesFab);

        // Set FireBase authentication
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        showMenu();
        showThemeMenu();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        defaultFab.setOnClickListener((v) -> {
            rootContent.setBackgroundResource(0);
        });

        christmasFab.setOnClickListener((v) -> {
            rootContent.setBackground(getResources().getDrawable(R.drawable.christmas_theme));
        });

        halloweenFab.setOnClickListener((v) -> {
            rootContent.setBackground(getResources().getDrawable(R.drawable.halloween_theme));
        });

        newyearsFab.setOnClickListener((v) -> {
            rootContent.setBackground(getResources().getDrawable(R.drawable.new_years_theme));
        });

        stpatFab.setOnClickListener((v) -> {
            rootContent.setBackground(getResources().getDrawable(R.drawable.st_pat_theme));
        });

        valentinesFab.setOnClickListener((v) -> {
            rootContent.setBackground(getResources().getDrawable(R.drawable.valentines_theme));
        });

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

            onGetUserProfileClicked();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            User.setGeneratedTerm("Weekly");
            getTopArtist("short");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getTopTracks("short");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getRecommendations();
            closeMenu();
        });

        mediumTermFab.setOnClickListener((v) -> {

            onGetUserProfileClicked();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            User.setGeneratedTerm("Monthly");
            getTopArtist("medium");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getTopTracks("medium");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getRecommendations();
            closeMenu();
        });

        longTermFab.setOnClickListener((v) -> {

            onGetUserProfileClicked();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            User.setGeneratedTerm("Yearly");
            getTopArtist("long");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getTopTracks("long");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getRecommendations();
            closeMenu();
        });
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
     * Get top 5 recently listened to artists
     */
    public void getTopArtist(String term) {
        if (User.accessToken == null) {
            Toast.makeText(this, "You need to link your account first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=" + term + "_term&limit=5&offset=0")
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

                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> artistId = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject artistInfo = jsonItems.getJSONObject(i);
                        String name = artistInfo.getString("name");

                        JSONObject topArtist = jsonItems.getJSONObject(i);
                        JSONArray artistImages = topArtist.getJSONArray("images");
                        JSONObject artistImage = artistImages.getJSONObject(0);
                        String imageURL = artistImage.getString("url");

                        JSONArray genreList = artistInfo.getJSONArray("genres");
                        String[] genre = new String[genreList.length()];
                        for (int j = 0; j < genreList.length(); j++) {
                            genre[j] = genreList.getString(j);
                        }

                        artistId.add(artistInfo.getString("id"));
                        names.add(name);
                        genres.addAll(Arrays.asList(genre));

                        if (i == 0) {
                            setTextAsync(name, topArtist1);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(name, topArtist3);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage3);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(name, topArtist2);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage2);
                                }
                            });
                        }

                        if (i == 3) {
                            setTextAsync(name, topArtist4);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage4);
                                }
                            });
                        }

                        if (i == 4) {
                            setTextAsync(name, topArtist6);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage6);
                                }
                            });
                        }

                        if (i == 5) {
                            setTextAsync(name, topArtist5);
                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(artistImage5);
                                }
                            });
                        }
                    }

                    User.setArtistId(artistId);

                    ArrayList<String> mode = informationFetcher.top3Genre(genres);

                    for (int i = 0; i < mode.size(); i++) {
                        if (i == 0) {
                            setTextAsync(mode.get(i), genre2);
                        }

                        if (i == 1) {
                            setTextAsync(mode.get(i), genre1);
                        }

                        if (i == 2) {
                            setTextAsync(mode.get(i), genre3);
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
     * Get top 3 recently listened to tracks
     */
    public void getTopTracks(String term) {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to link your account first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=" + term + "_term&limit=5&offset=0")
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

                    ArrayList<String> trackId = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        ArrayList<String> artistNames = new ArrayList<>();
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

                        if (i == 3) {
                            setTextAsync(name + "\n" + artists, textViewSong4);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView4);
                                }
                            });
                        }

                        if (i == 4) {
                            setTextAsync(name + "\n" + artists, textViewSong5);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(imageView5);
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
     * Get recommendations
     */
    public void getRecommendations() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to link your account first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String artistIdUrl = informationFetcher.artistIdUrl(User.getArtistId());
        String genreUrl = informationFetcher.genreUrl(User.getGenres());

        String url =
                "https://api.spotify.com/v1/recommendations?limit=6&seed_artists=" + artistIdUrl
                            + "&seed_genres=" + genreUrl;

        // get request
        final Request request = new Request.Builder()
                .url(url)
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
                    JSONArray trackList = jsonObject.getJSONArray("tracks");

                    int length = trackList.length();

                    for (int i = 0; i < length; i++) {
                        ArrayList<String> artistNames = new ArrayList<>();
                        JSONObject trackInfo = trackList.getJSONObject(i);
                        JSONArray artistInfo = trackInfo.getJSONArray("artists");

                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray imageInfo = albumInfo.getJSONArray("images");
                        String imageURL = (imageInfo.getJSONObject(0).getString("url"));

                        for (int j = 0; j < artistInfo.length(); j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");

                        if (name.contains("(")) {
                            name = name.substring(0, name.indexOf("(") - 1);
                        }

                        String output = name + "\n" + artistNames.get(0);

                        if (i == 0) {
                            setTextAsync(output, recText1);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage1);
                                }
                            });
                        }

                        if (i == 1) {
                            setTextAsync(output, recText2);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage2);
                                }
                            });
                        }

                        if (i == 2) {
                            setTextAsync(output, recText3);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage3);
                                }
                            });
                        }

                        if (i == 3) {
                            setTextAsync(output, recText4);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage4);
                                }
                            });
                        }

                        if (i == 4) {
                            setTextAsync(output, recText5);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage5);
                                }
                            });
                        }

                        if (i == 5) {
                            setTextAsync(output, recText6);

                            // Setting Image
                            Handler uiHandler = new Handler(Looper.getMainLooper());
                            uiHandler.post(new Runnable(){
                                @Override
                                public void run() {
                                    Picasso.get()
                                            .load(imageURL)
                                            .resize(300, 300)
                                            .centerCrop()
                                            .into(recImage6);
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

    public void onGetUserProfileClicked() {
        if (User.getAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
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
                    JSONArray imageData = jsonObject.getJSONArray("images");
                    if (imageData.length() != 0) {
                        User.setProfileImage(imageData.getJSONObject(0).getString("url"));
                    }
                    User.setDisplayName(jsonObject.getString("display_name"));

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
        fname = "";
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Wrapped!");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        fname = "Image-" + n + ".jpg";
        File file = new File (myDir, fname);
        // if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            uploadImage();
            if (!User.isPrivateAcc()) {
                uploadImagePublic();
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is immediately available to the user.
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
        SimpleDateFormat titleBuild = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
        Date date = new Date();
        Date date2 = new Date();
        Date date3 = new Date();
        String fileName = formatter.format(date);
        String titleName = titleBuild.format(date2);
        String timeName = timeStamp.format(date3);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference(User.currentUserId() + "/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                String generatedFilePath = downloadUri.getResult().toString();

                                WrappedFeed wrappedFeed = new WrappedFeed(User.getProfileImage(), generatedFilePath, User.getDisplayName() + ": " + titleName, User.getGeneratedTerm(), timeName);

                                firebaseFirestore.collection(user.getUid()).document(fileName).set(wrappedFeed);
                            }
                        });
                    }
                });
    }

    private void uploadImagePublic() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy__MM__dd_HH_mm_ss", Locale.ENGLISH);
        SimpleDateFormat titleBuild = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
        Date date = new Date();
        Date date2 = new Date();
        Date date3 = new Date();
        String fileName = formatter.format(date);
        String titleName = titleBuild.format(date2);
        String timeName = timeStamp.format(date3);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("Public Storage/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

                        downloadUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                String generatedFilePath = downloadUri.getResult().toString();

                                WrappedFeed wrappedFeed = new WrappedFeed(User.getProfileImage(), generatedFilePath, User.getDisplayName() + ": " + titleName, User.getGeneratedTerm(), timeName);

                                firebaseFirestore.collection("Public Storage").document(fileName).set(wrappedFeed);
                            }
                        });
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
        progressBar.setVisibility(View.VISIBLE);
        menuOpen = !menuOpen;
        shortTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        mediumTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        longTermFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu() {
        progressBar.setVisibility(View.INVISIBLE);
        menuOpen = !menuOpen;
        shortTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        mediumTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        longTermFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void showThemeMenu() {
        themesFab = findViewById(R.id.themesFab);
        defaultFab = findViewById(R.id.default_fab);
        christmasFab = findViewById(R.id.christmas_fab);
        halloweenFab = findViewById(R.id.halloween_fab);
        newyearsFab = findViewById(R.id.newyears_fab);
        stpatFab = findViewById(R.id.stpatrick_fab);
        valentinesFab = findViewById(R.id.valentines_fab);

        defaultFab.setAlpha(0f);
        christmasFab.setAlpha(0f);
        halloweenFab.setAlpha(0f);
        newyearsFab.setAlpha(0f);
        stpatFab.setAlpha(0f);
        valentinesFab.setAlpha(0f);

        defaultFab.setTranslationY(translationYaxis);
        christmasFab.setTranslationY(translationYaxis);
        halloweenFab.setTranslationY(translationYaxis);
        newyearsFab.setTranslationY(translationYaxis);
        stpatFab.setTranslationY(translationYaxis);
        valentinesFab.setTranslationY(translationYaxis);

        themesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (themeOpen) {
                    closeTheme();
                }

                else {
                    openTheme();
                }
            }
        });
    }

    private void openTheme() {
        themeOpen = !themeOpen;

        defaultFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        christmasFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        halloweenFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        newyearsFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        stpatFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        valentinesFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeTheme() {
        themeOpen = !themeOpen;

        defaultFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        christmasFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        halloweenFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        newyearsFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        stpatFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        valentinesFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
