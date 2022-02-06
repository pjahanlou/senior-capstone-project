package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainSettings extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userPicture;
    private TextView usernameText, emailText;
    private Button profileButton, appButton, logoutButton;

    private FirebaseDatabase database;
    private static DatabaseReference userTable;
    private FirebaseUser currentUser;
    private String currentUserUID;

    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        // Getting user info
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        email = currentUser.getEmail();
        username = currentUser.getDisplayName();

        // Initializing the views
        userPicture = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        profileButton = findViewById(R.id.profileSettingsButton);
        appButton = findViewById(R.id.appSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Adding click listeners to the buttons and imageview
        profileButton.setOnClickListener(this);
        appButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        // Retrieving the username and email
        setUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profileSettingsButton:
                startActivity(new Intent(MainSettings.this, ProfileSettings.class));
                break;
            case R.id.appSettingsButton:
                startActivity(new Intent(MainSettings.this, AppSettings.class));
                break;
            case R.id.logoutButton:
                logoutUser();
                break;
        }
    }

    /**
     * Method for setting the username and email in UI
     */
    private void setUserInfo(){
        usernameText.setText("Hello " + username + "!");
        emailText.setText(email);
    }

    /**
     * Method for logging out the user
     */
    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainSettings.this, LoginPage.class));
    }
}