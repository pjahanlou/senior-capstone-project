package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WriteReadPermission extends AppCompatActivity implements View.OnClickListener {

    private static final int WRITE_PERMISSION_CODE = 200;
    private Button sureButton, notSureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readwrite_permission);

        // Initializing the Buttons
        sureButton = findViewById(R.id.acceptWritePermission);
        notSureButton = findViewById(R.id.rejectWritePermission);

        // Adding click listener to the buttons
        sureButton.setOnClickListener(this);
        notSureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptWritePermission:
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERMISSION_CODE);
                // checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE);
                break;
            case R.id.rejectWritePermission:
                startActivity(new Intent(this, ProfileSettings.class));
                break;
        }
    }

        /**
         * Method for accepting location permission
         * */
        private void checkPermission (String permission,int requestCode){
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProfileSettings.class));
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults)
        {
            super.onRequestPermissionsResult(requestCode,
                    permissions,
                    grantResults);

            if (requestCode == WRITE_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Write Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Write Permission Denied", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(this, TextPermission.class));
            } /*else if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Coarse Location Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Coarse Location Permission Denied", Toast.LENGTH_SHORT).show();
                }

            }
            */
        }
    }
