package com.example.wrappedify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wrappedify.firebaseLogin.Login;
import com.example.wrappedify.firebaseLogin.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;

public class dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<WrappedFeed> arrayList;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final String CLIENT_ID = "ccb7c7bbeb9d455e96a4fbaac95885f1";
    public static final String REDIRECT_URI = "wrappedify://auth";

    boolean menuOpen = false;
    float translationYaxis = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    FloatingActionButton menuFab;
    FloatingActionButton generateWrappedFab;
    FloatingActionButton settingsFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (User.getAccessToken() == null) {
            Toast.makeText(dashboard.this, "Linking Account...", Toast.LENGTH_SHORT).show();
            getToken();
        }

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        menuFab = findViewById(R.id.menuFab);
        generateWrappedFab = findViewById(R.id.generateWrappedFab);
        settingsFab = findViewById(R.id.settingsFab);

        showMenu();

        arrayList.add(new WrappedFeed(R.drawable.ic_launcher_background, R.drawable.post, "title", "message"));

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        generateWrappedFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), generateWrapped.class);
            startActivity(intent);
            finish();
        });

        settingsFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        });
    }

    private void showMenu() {

        menuFab = findViewById(R.id.menuFab);
        generateWrappedFab = findViewById(R.id.generateWrappedFab);
        settingsFab = findViewById(R.id.settingsFab);

        generateWrappedFab.setAlpha(0f);
        settingsFab.setAlpha(0f);

        generateWrappedFab.setTranslationY(translationYaxis);
        settingsFab.setTranslationY(translationYaxis);

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

        generateWrappedFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), generateWrapped.class);
            startActivity(intent);
            finish();
        });

        settingsFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        });
    }

    private void openMenu() {
        menuOpen = !menuOpen;
        generateWrappedFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        settingsFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu() {
        menuOpen = !menuOpen;
        generateWrappedFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        settingsFab.animate().translationY(translationYaxis).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(dashboard.this, AUTH_TOKEN_REQUEST_CODE, request);
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

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
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
}