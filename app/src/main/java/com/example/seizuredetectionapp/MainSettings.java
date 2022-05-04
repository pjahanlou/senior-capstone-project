package com.example.seizuredetectionapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainSettings extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userPicture;
    private TextView usernameText, emailText;
    private Button profileButton, appButton, logoutButton;

    private FirebaseUser currentUser;

    private String username, email;

    private Uri imageUri;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> someActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        // initializing firebase cloud storage
        storageReference = FirebaseStorage.getInstance().getReference();

        // Getting user info
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        email = currentUser.getEmail();

        SharedPreferences sharedPreferences = getSharedPreferences (LocalSettings.PREFERENCES, MODE_PRIVATE);
        username = sharedPreferences.getString("name", LocalSettings.name);

        // Initializing the views
        userPicture = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        profileButton = findViewById(R.id.profileSettingsButton);
        appButton = findViewById(R.id.appSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Adding click listeners to the buttons and imageview
        userPicture.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        appButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        // Change the image view to user image
                        imageUri = data.getData();
                        userPicture.setImageURI(imageUri);

                        // Uploading user image to firebase cloud storage
                        storageReference.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Toast.makeText(this, "Picture uploaded successfully!", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Picture upload failed!", Toast.LENGTH_LONG).show();
                        });
                    }
                });

        // Retrieving the username and email
        setUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profileImage:
                selectImage();
                break;
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

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);

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
        Intent intent = new Intent(this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}