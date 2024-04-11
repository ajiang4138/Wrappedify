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

public class editEmail extends AppCompatActivity {

    ImageView backBtn;
    EditText currentEmail, newEmail, confirmNewEmail;
    Button confirmBtn;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_email);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        currentEmail = findViewById(R.id.editTextCurrentEmail);
        newEmail = findViewById(R.id.editTextNewEmail);
        confirmNewEmail = findViewById(R.id.editTextConfirmNewEmail);
        confirmBtn = findViewById(R.id.confirmBtn);


        backBtn.setOnClickListener((v) -> {
            finish();
        });

        confirmBtn.setOnClickListener((v) -> {
            changeEmail();
        });

    }

    private void changeEmail() {
        if (!(user.getEmail().equals(currentEmail.getText().toString()))) {
            Toast.makeText(editEmail.this, "Email does not match current email.", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (!(newEmail.getText().toString().equals(confirmNewEmail.getText().toString()))) {
            Toast.makeText(editEmail.this, "Emails do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            user.updateEmail(newEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(editEmail.this, "Email has been updated.", Toast.LENGTH_SHORT).show();
                            User.setAccessToken(null);
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });

        }

        return;
    }
}