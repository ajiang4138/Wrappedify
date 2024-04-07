package com.example.wrappedify.settingsPage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wrappedify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class deleteAccount extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);

        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        deleteButton.setOnClickListener((v) -> {
            confirmDelete();
        });

        cancelButton.setOnClickListener((v) -> {
            finish();
        });
    }

    public void confirmDelete() {
        return;
    }
}