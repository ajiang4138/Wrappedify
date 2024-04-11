package com.example.wrappedify.settingsPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wrappedify.R;
import com.example.wrappedify.firebaseLogin.Login;
import com.example.wrappedify.firebaseLogin.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editPassword extends AppCompatActivity {

    ImageView backBtn;
    Button confirmBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        confirmBtn = findViewById(R.id.confirmBtn);

        backBtn.setOnClickListener((v) -> {
            finish();
        });

        confirmBtn.setOnClickListener((v) -> {
            resetPassword();
            finish();
        });

    }

    private void resetPassword() {
        String email = user.getEmail();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(editPassword.this, "Password Reset Email Sent!", Toast.LENGTH_SHORT).show();
                            User.setAccessToken(null);
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}