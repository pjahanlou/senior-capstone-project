package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener {

    private TextView changeEmailText, changeDisplayNameText;
    private Button updateEmailButton, updateDisplayNameButton, changePasswordButton, deleteAccountButton;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // initializing firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initializing the views
        changeEmailText = findViewById(R.id.changeEmailText);
        changeDisplayNameText = findViewById(R.id.changeDisplayNameText);
        updateEmailButton = findViewById(R.id.submitNewEmail);
        updateDisplayNameButton = findViewById(R.id.submitNewDisplayName);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Setting the on click listeners
        updateEmailButton.setOnClickListener(this);
        updateDisplayNameButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submitNewEmail:
                updateEmail();
                break;
            case R.id.submitNewDisplayName:
                updateDisplayName();
                break;
            case R.id.changePasswordButton:
                startActivity(new Intent(ProfileSettings.this, ForgetPassword.class));
                break;
            case R.id.deleteAccountButton:
                deleteAccount();
                startActivity(new Intent(ProfileSettings.this, LoginPage.class));
                break;
        }
    }

    /**
     * Method for handling display name updates
     *
     * */
    private void updateDisplayName(){
        String TAG = "New Display Name";
        String newDisplayName = changeDisplayNameText.getText().toString().trim();

        if(newDisplayName.isEmpty()){
            changeDisplayNameText.setError("Display name is required!");
            changeDisplayNameText.requestFocus();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build();

        // Update the user profile in Firebase Authentication
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Display name updated.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User profile updated.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Display name update failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });

    }

    /**
     * Method for handling email update
     * */
    private void updateEmail(){
        String TAG = "New Email Address";
        String newEmail = changeEmailText.getText().toString().trim();

        // Validating user input
        if(newEmail.isEmpty()){
            changeEmailText.setError("Email is required!");
            changeEmailText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
            changeEmailText.setError("Please provide a valid email address");
            changeEmailText.requestFocus();
            return;
        }

        // Updating their email in Firebase Authentication
        currentUser.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Email updated.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User email address updated.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Email update failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });

    }

    /**
     * Method for handling user deleting their account
     * */
    private void deleteAccount(){
        String TAG = "Delete Account";

        currentUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Account deleted.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User account deleted.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Delete account failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });
    }

}