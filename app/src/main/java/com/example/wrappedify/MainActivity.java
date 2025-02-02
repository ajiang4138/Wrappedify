package com.example.wrappedify;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wrappedify.firebaseLogin.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "ccb7c7bbeb9d455e96a4fbaac95885f1";
    public static final String CLIENT_SECRET = "6daf73f2aea74601bd4925a1d1430294";
    public static final String REDIRECT_URI = "wrappedify://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private TextView tokenTextView, codeTextView, profileTextView, mediumTermTextView, mediumTracksTextView;

    private ImageView artistPict;

    private WebView myWebView;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String, Object> dataMap = new HashMap<>();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        // Initialize the views
        tokenTextView = findViewById(R.id.token_text_view);
        profileTextView = findViewById(R.id.response_text_view);
        mediumTermTextView = findViewById(R.id.medium_text_view);
        mediumTracksTextView = findViewById(R.id.mediumTracks_text_view);

        artistPict = findViewById(R.id.artistImage);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVisibility(View.GONE);

        // Initialize the buttons

        Button tokenBtn = findViewById(R.id.token_btn);
        Button profileBtn = findViewById(R.id.profile_btn);

        Button unlinkBtn = findViewById(R.id.unlink_btn);

        Button shortBtn = findViewById(R.id.short_term_btn);
        Button mediumBtn = findViewById(R.id.medium_term_btn);
        Button longBtn = findViewById(R.id.long_term_btn);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        
        Button dashboardBtn = findViewById(R.id.dashboardBtn);


        // Set the click listeners for the buttons

        tokenBtn.setOnClickListener((v) -> {
            getToken();
        });

        profileBtn.setOnClickListener((v) -> {
            onGetUserProfileClicked();
        });

        unlinkBtn.setOnClickListener((v) -> {
            unlinkAccount();
        });

        shortBtn.setOnClickListener((v) -> {
            getShortTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getShortTopTracks();
        });

        mediumBtn.setOnClickListener((v) -> {
            getMediumTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getMediumTopTracks();
        });

        longBtn.setOnClickListener((v) -> {
            getLongTopArtist();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getLongTopTracks();
        });

        logoutBtn.setOnClickListener((v) -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        dashboardBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), dashboard.class);
            startActivity(intent);
            finish();
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
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    public void unlinkAccount() {
        mAccessToken = null;
        myWebView.setVisibility(View.VISIBLE);
        myWebView.loadUrl("https://www.spotify.com/us/logout");

        setTextAsync(" ", tokenTextView);

        Toast.makeText(this, "Account Unlinked!", Toast.LENGTH_SHORT).show();
        myWebView.setVisibility(View.GONE);
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                setTextAsync(response.body().string(), profileTextView);

            }
        });
    }

    /**
     * Get top 5 recently listened to artists, by short term.
     */
    public void getShortTopArtist() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=short_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    /**
                     * Setting image
                     */
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistPict);
                        }
                    });

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

                        output += "Artist " + (i + 1) + ": " + name + " Genres: " + Arrays.toString(genre) + "\n";
                    }

                    ArrayList<String> mode = informationFetcher.top3Genre(genres);

                    output += "Most commonly listened to genre: " + mode + "\n";

                    setTextAsync(output, mediumTermTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 5 recently listened to tracks, by short term
     */
    public void getShortTopTracks() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");
                        int artistInfoLength = artistInfo.length();

                        for (int j = 0; j < artistInfoLength; j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");

                        names.add(name + " by " + artistNames + "\n\n");
                    }

                    output += "Tracks\n " + names;

                    setTextAsync(output, mediumTracksTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
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
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=medium_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistPict);
                        }
                    });

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

                        output += "Artist " + (i + 1) + ": " + name + " Genres: " + Arrays.toString(genre) + "\n";
                    }

                    ArrayList<String> mode = informationFetcher.top3Genre(genres);

                    output += "Most commonly listened to genre: " + mode + "\n";

                    setTextAsync(output, mediumTermTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
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
    public void getMediumTopTracks() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=medium_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");
                        int artistInfoLength = artistInfo.length();

                        for (int j = 0; j < artistInfoLength; j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");


                        names.add(name + " by " + artistNames + "\n\n");
                    }

                    output += "Tracks\n " + names;

                    setTextAsync(output, mediumTracksTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
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
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();

                    JSONObject topArtist = jsonItems.getJSONObject(0);
                    JSONArray artistImages = topArtist.getJSONArray("images");
                    JSONObject artistImage = artistImages.getJSONObject(0);
                    String imageURL = artistImage.getString("url");

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            Picasso.get()
                                    .load(imageURL)
                                    .resize(300, 300)
                                    .centerCrop()
                                    .into(artistPict);
                        }
                    });

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

                        output += "Artist " + (i + 1) + ": " + name + " Genres: " + Arrays.toString(genre) + "\n";
                    }

                    ArrayList<String> mode = informationFetcher.top3Genre(genres);

                    output += "Most commonly listened to genre: " + mode + "\n";

                    setTextAsync(output, mediumTermTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Get top 5 recently listened to tracks, by long term
     */
    public void getLongTopTracks() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // get request
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=5&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonItems = jsonObject.getJSONArray("items");
                    int length = jsonItems.length();

                    String output = "";
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> artistNames = new ArrayList<>();

                    for (int i = 0; i < length; i++) {
                        JSONObject trackInfo = jsonItems.getJSONObject(i);
                        JSONObject albumInfo = trackInfo.getJSONObject("album");
                        JSONArray artistInfo = albumInfo.getJSONArray("artists");
                        int artistInfoLength = artistInfo.length();

                        for (int j = 0; j < artistInfoLength; j++) {
                            JSONObject artist = artistInfo.getJSONObject(j);
                            artistNames.add(artist.getString("name"));
                        }

                        String name = trackInfo.getString("name");

                        names.add(name + " by " + artistNames + "\n\n");
                    }

                    output += "Tracks\n " + names;

                    setTextAsync(output, mediumTracksTextView);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
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

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    private void setImageAsync(final Drawable image, ImageView imageView) {
        runOnUiThread(() -> imageView.setImageDrawable(image));
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

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
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