package com.example.wrappedify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.wrappedify.settingsPage.SecurityPrivacy;
import com.example.wrappedify.settingsPage.deleteAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView username = (TextView) findViewById(R.id.username);
        Button editProfileBtn = (Button) findViewById(R.id.editProfileBtn);
        SwitchCompat nightToggle = (SwitchCompat) findViewById(R.id.nightModeSwitch);
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        RelativeLayout securityAndPrivacy = findViewById(R.id.securityAndPrivacy);

        username.setText(user.getEmail());

        editProfileBtn.setOnClickListener((v) -> {
            goEditProfile();
        });

        securityAndPrivacy.setOnClickListener((v) -> {
            goSecurity();
        });

        backBtn.setOnClickListener((v) -> {
            finish();
        });

    }

    public void goEditProfile() {
        return;
    }

    public void goSecurity() {
        Intent intent = new Intent(getApplicationContext(), SecurityPrivacy.class);
        startActivity(intent);
    }
    public void goEditAccount() {
        Intent intent = new Intent(getApplicationContext(), deleteAccount.class);
        startActivity(intent);
    }


}
