package com.example.wrappedify.settingsPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wrappedify.R;
import com.example.wrappedify.Settings;
import com.example.wrappedify.firebaseLogin.Login;
import com.example.wrappedify.firebaseLogin.User;
import com.google.firebase.auth.FirebaseAuth;

public class SecurityPrivacy extends AppCompatActivity {

    RelativeLayout logoutBtn, unlinkAccBtn, deleteAcct;
    WebView myWebView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_priv);

        deleteAcct = findViewById(R.id.delete_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        unlinkAccBtn = findViewById(R.id.unlink_btn);

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
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        unlinkAccBtn.setOnClickListener((v) -> {
            unlinkAccount();
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

}
