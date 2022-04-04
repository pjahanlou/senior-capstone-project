package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TextPermission extends AppCompatActivity implements View.OnClickListener{

    private static final int TEXT_PERMISSION_CODE = 102;
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
                checkPermission(Manifest.permission.SEND_SMS, TEXT_PERMISSION_CODE);
                break;
            case R.id.rejectTextPermission:
                Intent intent = new Intent(this, UsualLocations.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Method for accepting location permission
     * */
    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UsualLocations.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == TEXT_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Text Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(this, "Text Permission Denied", Toast.LENGTH_SHORT) .show();
            }
            startActivity(new Intent(this, UsualLocations.class));
        }
    }
}