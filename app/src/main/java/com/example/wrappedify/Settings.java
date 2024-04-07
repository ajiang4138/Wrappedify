package com.example.wrappedify;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.wrappedify.settingsPage.SecurityPrivacy;

import com.example.wrappedify.firebaseLogin.Login;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wrappedify.firebaseLogin.User;
import com.example.wrappedify.firebaseLogin.UserModel;

import com.example.wrappedify.settingsPage.deleteAccount;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Settings extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    ImageView profilePic;

    UserModel currentUserModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        getUserData();

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(this, selectedImageUri, profilePic);
                        }
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView username = (TextView) findViewById(R.id.username);
        Button editProfileBtn = (Button) findViewById(R.id.editProfileBtn);
        SwitchCompat nightToggle = (SwitchCompat) findViewById(R.id.nightModeSwitch);
        ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
        RelativeLayout securityAndPrivacy = findViewById(R.id.securityAndPrivacy);
        profilePic = findViewById(R.id.profile_pic);

        username.setText(user.getEmail());

        // User Model setup
        currentUserModel = new UserModel();
        currentUserModel.setUserId(user.getEmail());

        editProfileBtn.setOnClickListener((v) -> {
            goEditProfile();
        });

        securityAndPrivacy.setOnClickListener((v) -> {
            goSecurity();
        });

        backBtn.setOnClickListener((v) -> {
            if (selectedImageUri != null) {
                User.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            updateToFirestore();
                        });
            }

            finish();
        });

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });

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

    public void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void updateToFirestore(){
        User.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(Settings.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserData() {
        User.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        setProfilePic(Settings.this, uri, profilePic);
                    }
                });
    }


}
