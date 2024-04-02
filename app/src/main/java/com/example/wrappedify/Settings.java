package com.example.wrappedify;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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

        username.setText(user.getEmail());

        editProfileBtn.setOnClickListener((v) -> {
            goEditProfile();
        });


    }

    public void goEditProfile() {
        return;
    }
}
