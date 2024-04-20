package com.example.wrappedify;

import static com.example.wrappedify.firebaseLogin.User.AUTH_TOKEN_REQUEST_CODE;
import static com.example.wrappedify.firebaseLogin.User.CLIENT_ID;
import static com.example.wrappedify.firebaseLogin.User.REDIRECT_URI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wrappedify.firebaseLogin.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Map;

public class dashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<WrappedFeed> feed;
    private RecyclerAdapter recyclerAdapter;

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
        firebaseFirestore = FirebaseFirestore.getInstance();
        feed = new ArrayList<WrappedFeed>();

        if (User.getAccessToken() == null) {
            Toast.makeText(dashboard.this, "Linking Account...", Toast.LENGTH_SHORT).show();
            getToken();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuFab = findViewById(R.id.menuFab);
        generateWrappedFab = findViewById(R.id.generateWrappedFab);
        settingsFab = findViewById(R.id.settingsFab);

        showMenu();

        recyclerAdapter = new RecyclerAdapter(dashboard.this, feed);
        recyclerView.setAdapter(recyclerAdapter);

        if (!User.isPrivateAcc()) {
            populateFeedPublic();
        }

        else {
            populateFeed();
        }

        generateWrappedFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), generateWrapped.class);
            startActivity(intent);
            finish();
        });

        settingsFab.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            finish();
        });
    }

    private void populateFeed() {

        firebaseFirestore.collection(user.getUid()).orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            /*
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                             */
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                feed.add(0, dc.getDocument().toObject(WrappedFeed.class));
                            }

                            recyclerAdapter.notifyDataSetChanged();
                            /*
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                             */

                         }

                    }
                });
    }

    private void populateFeedPublic() {

        firebaseFirestore.collection("Public Storage/").orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            /*
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                             */
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                feed.add(0, dc.getDocument().toObject(WrappedFeed.class));
                            }

                            recyclerAdapter.notifyDataSetChanged();
                            /*
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                             */

                        }

                    }
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
        AuthorizationClient.openLoginActivity(dashboard.this, User.AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (User.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            User.setAccessToken(response.getAccessToken());
        }
    }

    private Uri getRedirectUri() {
        return Uri.parse(User.REDIRECT_URI);
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(User.CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }
}