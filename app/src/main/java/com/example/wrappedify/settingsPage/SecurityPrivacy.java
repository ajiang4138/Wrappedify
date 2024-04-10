package com.example.wrappedify.settingsPage;

import static com.example.wrappedify.firebaseLogin.User.AUTH_TOKEN_REQUEST_CODE;
import static com.example.wrappedify.firebaseLogin.User.CLIENT_ID;
import static com.example.wrappedify.firebaseLogin.User.REDIRECT_URI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wrappedify.R;
import com.example.wrappedify.firebaseLogin.Login;
import com.example.wrappedify.firebaseLogin.User;
import com.google.firebase.auth.FirebaseAuth;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SecurityPrivacy extends AppCompatActivity {

    RelativeLayout logoutBtn, linkAccBtn, unlinkAccBtn, deleteAcct;
    WebView myWebView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_priv);

        deleteAcct = findViewById(R.id.delete_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        unlinkAccBtn = findViewById(R.id.unlink_btn);
        linkAccBtn = findViewById(R.id.link_btn);

        myWebView = findViewById(R.id.webview);
        myWebView.setVisibility(View.GONE);

        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener((v) -> {
            finish();
        });

        deleteAcct.setOnClickListener((v) -> {
            goDeleteAcct();
        });

        logoutBtn.setOnClickListener((v) -> {
            User.setAccessToken(null);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        unlinkAccBtn.setOnClickListener((v) -> {
            unlinkAccount();
        });

        linkAccBtn.setOnClickListener((v) -> {
            if (User.getAccessToken() == null) {
                Toast.makeText(SecurityPrivacy.this, "Linking Account...", Toast.LENGTH_SHORT).show();
                getToken();
            }

            else {
                Toast.makeText(SecurityPrivacy.this, "Account Already Linked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goDeleteAcct() {
        Intent intent = new Intent(getApplicationContext(), deleteAccount.class);
        startActivity(intent);
    }

    public void unlinkAccount() {
        User.setAccessToken(null);
        myWebView.setVisibility(View.VISIBLE);
        myWebView.loadUrl("https://www.spotify.com/us/logout");

        Toast.makeText(this, "Account Unlinked!", Toast.LENGTH_SHORT).show();
        myWebView.setVisibility(View.GONE);
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(SecurityPrivacy.this, AUTH_TOKEN_REQUEST_CODE, request);
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
}
