package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactPermission extends AppCompatActivity implements View.OnClickListener{

    private Button sureButton, notSureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_permission);

        // Initializing the Buttons
        sureButton = findViewById(R.id.acceptContactPermission);
        notSureButton = findViewById(R.id.rejectContactPermission);

        // Adding click listener to the buttons
        sureButton.setOnClickListener(this);
        notSureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.acceptContactPermission:
                getContactPermission();
                break;
            case R.id.rejectContactPermission:
                break;
        }
        startActivity(new Intent(this, LocationPermission.class));
    }

    /**
     * Method for accepting contact permission
     * */
    private void getContactPermission() {
    }
}