package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationPermission extends AppCompatActivity implements View.OnClickListener{

    private Button sureButton, notSureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        // Initializing the Buttons
        sureButton = findViewById(R.id.acceptLocationPermission);
        notSureButton = findViewById(R.id.rejectLocationPermission);

        // Adding click listener to the buttons
        sureButton.setOnClickListener(this);
        notSureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.acceptLocationPermission:
                getLocationPermission();
                break;
            case R.id.rejectLocationPermission:
                break;
        }
        startActivity(new Intent(this, TextPermission.class));
    }

    /**
     * Method for accepting location permission
     * */
    private void getLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
    }
}