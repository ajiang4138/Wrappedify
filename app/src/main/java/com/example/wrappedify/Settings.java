package com.example.wrappedify;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.wrappedify.settingsPage.SecurityPrivacy;

import com.example.wrappedify.firebaseLogin.Login;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wrappedify.firebaseLogin.User;
import com.example.wrappedify.firebaseLogin.UserModel;

import com.example.wrappedify.settingsPage.deleteAccount;
import com.example.wrappedify.settingsPage.editPassEmailQuery;
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

    private static final String NIGHT_MODE = "night_mode";
    private SwitchCompat nightModeSwitch;
    private SharedPreferences sharedPreferences;

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        button = findViewById(R.id.btnNotifications);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(Settings.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Settings.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchCompat notificationSwitch = findViewById(R.id.notificationSwitch);
                if (notificationSwitch.isChecked()) {
                    makeNotificationDelayed();
                }
            }
        });

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

        TextView username = findViewById(R.id.username);
        Button editProfileBtn = findViewById(R.id.editProfileBtn);
        ImageView backBtn = findViewById(R.id.backBtn);
        RelativeLayout securityAndPrivacy = findViewById(R.id.securityAndPrivacy);
        profilePic = findViewById(R.id.profile_pic);

        // Night Mode
        nightModeSwitch = findViewById(R.id.nightModeSwitch);
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        nightModeSwitch.setChecked(sharedPreferences.getBoolean(NIGHT_MODE, false));

        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NIGHT_MODE, isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

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

    public void makeNotification() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Happy Holidays!")
                .setContentText("Check out our new special holiday versions coming soon!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID,
                        "some description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationManager.notify(0, builder.build());

    }

    public void makeNotificationDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeNotification();
            }
        }, 5000);
    }


    public void goEditProfile() {
        Intent intent = new Intent(getApplicationContext(), editPassEmailQuery.class);
        startActivity(intent);
    }

    public void goSecurity() {
        Intent intent = new Intent(getApplicationContext(), SecurityPrivacy.class);
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
