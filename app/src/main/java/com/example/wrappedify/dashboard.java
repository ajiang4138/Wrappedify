package com.example.wrappedify;

import static com.example.wrappedify.firebaseLogin.User.AUTH_TOKEN_REQUEST_CODE;
import static com.example.wrappedify.firebaseLogin.User.CLIENT_ID;
import static com.example.wrappedify.firebaseLogin.User.REDIRECT_URI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wrappedify.firebaseLogin.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Map;

public class dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<WrappedFeed> feed = new ArrayList<>();

    boolean menuOpen = false;
    float translationYaxis = 100f;
    OvershootInterpolator interpolator = new OvershootInterpolator();

    FloatingActionButton menuFab;
    FloatingActionButton generateWrappedFab;
    FloatingActionButton settingsFab;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (User.getAccessToken() == null) {
            Toast.makeText(dashboard.this, "Linking Account...", Toast.LENGTH_SHORT).show();
            getToken();
        }

        recyclerView = findViewById(R.id.recyclerView);

        menuFab = findViewById(R.id.menuFab);
        generateWrappedFab = findViewById(R.id.generateWrappedFab);
        settingsFab = findViewById(R.id.settingsFab);

        showMenu();
        populateFeed();

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(feed);
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

    public void populateFeed() {

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