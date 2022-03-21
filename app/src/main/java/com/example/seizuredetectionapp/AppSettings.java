package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class AppSettings extends AppCompatActivity implements View.OnClickListener{

    private TextView nameTextView, countdownTimerTextView, ageTextView,
        seizureDurationTextView, heightTextView, weightTextView,
        seizureFrequencyTextView;
    private Button submitNewName, submitNewCountdownTimer, submitNewAge,
        submitNewSeizureDuration, submitNewHeight, submitNewWeight,
        submitNewSeizureFrequency, changeContactList;
    private PowerSpinnerView prefContactMethodDropDown;

    private String currentUserUID;
    private FirebaseDatabase database;
    private DatabaseReference settingsTable;

    private LocalSettings localSettings;

    private Questionnaire settings = new Questionnaire();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // Initializing settings
        localSettings = (LocalSettings) getApplication();

        // Initializing Firebase
        // currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // database = FirebaseDatabase.getInstance();
        // settingsTable = database.getReference("Users").child(currentUserUID).child("Settings");

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

        // Initializing the dropdown
        prefContactMethodDropDown = findViewById(R.id.prefContactMethod);
        
        // Adding event listeners to the buttons and dropdowns
        submitNewName.setOnClickListener(this);
        submitNewCountdownTimer.setOnClickListener(this);
        submitNewAge.setOnClickListener(this);
        submitNewSeizureDuration.setOnClickListener(this);
        submitNewHeight.setOnClickListener(this);
        submitNewWeight.setOnClickListener(this);
        submitNewSeizureFrequency.setOnClickListener(this);
        changeContactList.setOnClickListener(this);

        prefContactMethodDropDown.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                localSettings.setPreferredContactMethod(newItem);
                Log.d("pref", ""+newItem);
                SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(LocalSettings.DEFAULT, localSettings.getPreferredContactMethod());
                editor.apply();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submitNewName:
                updateFieldInFirebase("name", nameTextView);
                break;
            case R.id.submitNewCountdownTimer:
                updateFieldInFirebase("countdown timer", countdownTimerTextView);
                break;
            case R.id.prefContactMethod:

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
                startActivity(new Intent(AppSettings.this, UpdateContacts.class));
                break;
        }
    }

    private void updateFieldInFirebase(String field, TextView textview){
        String value = textview.getText().toString().trim();

        if(value.isEmpty()){
            textview.setError("Field is required!");
            textview.requestFocus();
            return;
        }

        // Writing the new user data to shared preferences
        localSettings.setField(field, value);

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(field, localSettings.getField(field));
        editor.apply();

    }
}