package com.example.wrappedify.settingsPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wrappedify.R;
import com.example.wrappedify.firebaseLogin.Login;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class deleteAccount extends AppCompatActivity {

    EditText editTextPassword;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);

        editTextPassword = findViewById(R.id.editTextPassword);
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
        String password = editTextPassword.getText().toString();

        // Re-authenticate the user before deleting the account
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User re-authenticated, proceed with account deletion
                        user.delete()
                                .addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        // Account deleted successfully
                                        Toast.makeText(deleteAccount.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Start the Login activity and finish current activity
                                        Intent intent = new Intent(deleteAccount.this, Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Account deletion failed
                                        Toast.makeText(deleteAccount.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Re-authentication failed
                        Toast.makeText(deleteAccount.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}