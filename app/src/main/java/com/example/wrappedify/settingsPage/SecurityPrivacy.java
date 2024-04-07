package com.example.wrappedify.settingsPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wrappedify.R;
import com.example.wrappedify.Settings;

public class SecurityPrivacy extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_priv);
        RelativeLayout deleteAcct = findViewById(R.id.delete_btn);
        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        });

        deleteAcct.setOnClickListener((v) -> {
            goDeleteAcct();
        });
    }

    public void goDeleteAcct() {
        Intent intent = new Intent(getApplicationContext(), deleteAccount.class);
        startActivity(intent);
    }
}
