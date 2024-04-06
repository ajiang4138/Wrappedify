package com.example.wrappedify;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    private SwitchCompat nightModeSwitch;
    private SharedPreferences sharedPreferences;
    private static final String NIGHT_MODE = "night_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView username = findViewById(R.id.username);
        Button editProfileBtn = findViewById(R.id.editProfileBtn);
        ImageView backBtn = findViewById(R.id.backBtn);
        RelativeLayout securityAndPrivacy = findViewById(R.id.securityAndPrivacy);

        // Initialize night mode switch and shared preferences
        nightModeSwitch = findViewById(R.id.nightModeSwitch);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Set initial state of the switch based on saved preference
        nightModeSwitch.setChecked(sharedPreferences.getBoolean(NIGHT_MODE, false));

        // Set listener for the switch
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the state of the switch to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NIGHT_MODE, isChecked);
            editor.apply();

            // Apply appropriate theme based on the state of the switch
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Set text for username
        username.setText(user != null ? user.getEmail() : "");

        // Click listeners for buttons
        editProfileBtn.setOnClickListener((v) -> goEditProfile());
        securityAndPrivacy.setOnClickListener((v) -> goEditAccount());
        backBtn.setOnClickListener((v) -> finish());
    }

    public void goEditProfile() {
        // Add your functionality for editing profile here
    }

    public void goEditAccount() {
        // Add your functionality for editing account here
    }
}