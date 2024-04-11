package com.example.wrappedify.settingsPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wrappedify.R;

public class editPassEmailQuery extends AppCompatActivity {

    private ImageView backBtn;
    private RelativeLayout editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pass_email_query);

        backBtn = findViewById(R.id.backBtn);
        editEmail = findViewById(R.id.changeEmail);
        editPassword = findViewById(R.id.changePassword);

        backBtn.setOnClickListener((v) -> {
            finish();
        });

        editEmail.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), editEmail.class);
            startActivity(intent);
            finish();
        });

        editPassword.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), editPassword.class);
            startActivity(intent);
            finish();
        });

    }
}