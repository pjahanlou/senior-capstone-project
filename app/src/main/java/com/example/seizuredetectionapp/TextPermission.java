package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TextPermission extends AppCompatActivity implements View.OnClickListener{

    private Button sureButton, notSureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_permission);

        // Initializing the Buttons
        sureButton = findViewById(R.id.acceptTextPermission);
        notSureButton = findViewById(R.id.rejectTextPermission);

        // Adding click listener to the buttons
        sureButton.setOnClickListener(this);
        notSureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.acceptTextPermission:
                getTextPermission();
                break;
            case R.id.rejectTextPermission:
                break;
        }
        startActivity(new Intent(this, UsualLocations.class));
    }

    /**
     * Method for accepting contact permission
     * */
    private void getTextPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS}, 44);
    }
}