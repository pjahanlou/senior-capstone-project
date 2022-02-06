package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppSettings extends AppCompatActivity implements View.OnClickListener{

    private TextView nameTextView, countdownTimerTextView, ageTextView,
        seizureDurationTextView, heightTextView, weightTextView,
        seizureFrequencyTextView;
    private Button submitNewName, submitNewCountdownTimer, submitNewAge,
        submitNewSeizureDuration, submitNewHeight, submitNewWeight,
        submitNewSeizureFrequency, changeContactList;

    private String currentUserUID;
    private FirebaseDatabase database;
    private DatabaseReference settingsTable;

    private Questionnaire settings = new Questionnaire();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // Initializing Firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        settingsTable = database.getReference("Users").child(currentUserUID).child("Settings");

        // initializing the text views
        nameTextView = findViewById(R.id.nameTextView);
        countdownTimerTextView = findViewById(R.id.countdownTimerTextView);
        ageTextView = findViewById(R.id.ageTextView);
        seizureDurationTextView = findViewById(R.id.seizureDurationTextView);
        heightTextView = findViewById(R.id.heightTextView);
        weightTextView = findViewById(R.id.weightTextView);
        seizureFrequencyTextView = findViewById(R.id.seizureFrequencyTextView);

        // initializing the buttons
        submitNewName = findViewById(R.id.submitNewName);
        submitNewCountdownTimer = findViewById(R.id.submitNewCountdownTimer);
        submitNewAge = findViewById(R.id.submitNewAge);
        submitNewSeizureDuration = findViewById(R.id.submitNewSeizureDuration);
        submitNewHeight = findViewById(R.id.submitNewHeight);
        submitNewWeight = findViewById(R.id.submitNewWeight);
        submitNewSeizureFrequency = findViewById(R.id.submitNewSeizureFrequency);
        changeContactList = findViewById(R.id.changeContactList);

        // Adding event listeners to the buttons
        submitNewName.setOnClickListener(this);
        submitNewCountdownTimer.setOnClickListener(this);
        submitNewAge.setOnClickListener(this);
        submitNewSeizureDuration.setOnClickListener(this);
        submitNewHeight.setOnClickListener(this);
        submitNewWeight.setOnClickListener(this);
        submitNewSeizureFrequency.setOnClickListener(this);
        changeContactList.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submitNewName:
                updateFieldInFirebase("name", nameTextView);
                break;
            case R.id.submitNewCountdownTimer:
                updateFieldInFirebase("countdownTimer", countdownTimerTextView);
                break;
            case R.id.submitNewAge:
                updateFieldInFirebase("age", ageTextView);
                break;
            case R.id.submitNewSeizureDuration:
                updateFieldInFirebase("seizureDuration", seizureDurationTextView);
                break;
            case R.id.submitNewHeight:
                updateFieldInFirebase("height", heightTextView);
                break;
            case R.id.submitNewWeight:
                updateFieldInFirebase("weight", weightTextView);
                break;
            case R.id.submitNewSeizureFrequency:
                updateFieldInFirebase("seizureFrequencyPerMonth", seizureFrequencyTextView);
                break;
            case R.id.changeContactList:
                break;
        }
    }

    private void updateFieldInFirebase(String field, TextView textview){
        String newField = textview.getText().toString().trim();

        if(newField.isEmpty()){
            textview.setError("Field is required!");
            textview.requestFocus();
            return;
        }

        settingsTable.child(field).setValue(newField).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(AppSettings.this, field + " updated.", Toast.LENGTH_LONG).show();
                Log.d(field, "Updated successfully");
            }
            else{
                Toast.makeText(AppSettings.this, field + " update failed!", Toast.LENGTH_LONG).show();
                Log.d(field, task.getException().toString());
            }
        });

    }
}