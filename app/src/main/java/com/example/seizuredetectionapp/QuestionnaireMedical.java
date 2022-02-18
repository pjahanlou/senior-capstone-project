package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener{
    NumberPicker seizureDurationMinutes, seizureDurationSeconds, heightFeet, heightInches;
    EditText seizureFrequency, seizureStartDate, seizureStartYear, weightInput;
    Spinner seizureType, sexInput, seizureStartMonth;
    Button submitQuestionnaireMedical;
    private LocalSettings localSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_medical);

        seizureDurationMinutes = findViewById(R.id.seizureDurationMinutes);
        seizureDurationSeconds = findViewById(R.id.seizureDurationSeconds);
        heightFeet = findViewById(R.id.heightInputFeet);
        heightInches = findViewById(R.id.heightInputInches);
        weightInput = findViewById(R.id.weightInput);
        seizureFrequency = findViewById(R.id.seizureFrequency);
        seizureStartDate = findViewById(R.id.seizureStartDate);
        seizureStartMonth = findViewById(R.id.seizureStartMonth);
        seizureStartYear = findViewById(R.id.seizureStartYear);
        seizureType = findViewById(R.id.seizureType);
        sexInput = findViewById(R.id.sexInput);

        submitQuestionnaireMedical = findViewById(R.id.submitQuestionnaireMedical);

        seizureDurationMinutes.setMinValue(0);
        seizureDurationMinutes.setMaxValue(60);

        seizureDurationSeconds.setMinValue(0);
        seizureDurationSeconds.setMaxValue(59);

        heightFeet.setMinValue(0);
        heightFeet.setMaxValue(12);

        heightInches.setMinValue(0);
        heightInches.setMaxValue(11);

        submitQuestionnaireMedical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.submitQuestionnaireMedical: {
                String seizureDuration = String.valueOf(seizureDurationSeconds.getValue() + (seizureDurationMinutes.getValue() * 60));
                String height = String.valueOf(heightInches.getValue() + (heightFeet.getValue() * 12));
                String weight = weightInput.getText().toString().trim();
                String seizureFrequencyPerMonth = seizureFrequency.getText().toString().trim();
                String seizureStartD = seizureStartDate.getText().toString().trim();
                String seizureStartM = seizureStartMonth.getSelectedItem().toString().trim();
                String seizureStartY = seizureStartYear.getText().toString().trim();
                String seizureT = seizureType.getSelectedItem().toString().trim();
                String sex = sexInput.getSelectedItem().toString().trim();

                //checks to see if any inputs are empty and alerts user.
                if (height.equals("0")) {
                    heightFeet.requestFocus();
                    return;
                }

                if (weight.equals("0")) {
                    weightInput.requestFocus();
                    return;
                }

                // grab data from last questionnaire
                Intent i = getIntent();
                Questionnaire personalObject = (Questionnaire)i.getSerializableExtra("contactListObject");

                Questionnaire personal = new Questionnaire(personalObject.name, personalObject.addedContacts, personalObject.countdownTimer,
                        personalObject.age, personalObject.contactMethod, seizureDuration, height,
                        weight, seizureFrequencyPerMonth, seizureStartD,
                        seizureStartM, seizureStartY, seizureT, sex);

                Log.d("confirmation", "completed list: " + personal.toString());

                // Push to firebase
                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(currentUserUID).child("Settings");

                myRef.setValue(personal).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuestionnaireMedical.this, "Questionnaire saved.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(QuestionnaireMedical.this, Datatable.class));
                    }
                    else {
                        Toast.makeText(QuestionnaireMedical.this, "Questionnaire save failed.", Toast.LENGTH_LONG).show();
                    }
                });

                localSettings.setQuestionnaireComplete("1");

                SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(LocalSettings.DEFAULT, localSettings.getQuestionnaireComplete());
                editor.apply();
            }
        }
    }
}