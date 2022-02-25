package com.example.seizuredetectionapp;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Set;


public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    NumberPicker seizureDurationMinutes, seizureDurationSeconds, heightFeet, heightInches;
    EditText seizureFrequency, weightInput;
    Spinner seizureType, sexInput;
    Button submitQuestionnaireMedical, openDatePicker;
    String seizureStartD;
    private LocalSettings localSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_medical);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        seizureDurationMinutes = findViewById(R.id.seizureDurationMinutes);
        seizureDurationSeconds = findViewById(R.id.seizureDurationSeconds);
        heightFeet = findViewById(R.id.heightInputFeet);
        heightInches = findViewById(R.id.heightInputInches);
        weightInput = findViewById(R.id.weightInput);
        seizureFrequency = findViewById(R.id.seizureFrequency);
        seizureType = findViewById(R.id.seizureType);
        sexInput = findViewById(R.id.sexInput);
        seizureStartD = "";
        
        openDatePicker = findViewById(R.id.openDatePickerDialog);
        submitQuestionnaireMedical = findViewById(R.id.submitQuestionnaireMedical);

        seizureDurationMinutes.setMinValue(0);
        seizureDurationMinutes.setMaxValue(60);

        seizureDurationSeconds.setMinValue(0);
        seizureDurationSeconds.setMaxValue(59);

        heightFeet.setMinValue(0);
        heightFeet.setMaxValue(12);

        heightInches.setMinValue(0);
        heightInches.setMaxValue(11);

        openDatePicker.setOnClickListener(this);
        submitQuestionnaireMedical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openDatePickerDialog: {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        0,
                        this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();

                break;
            }
            
            case R.id.submitQuestionnaireMedical:
                saveQuestionnaireMedicalToFirebase();
                break;
        }
    }

    private void saveQuestionnaireMedicalToFirebase() {
        String seizureDuration = String.valueOf(seizureDurationSeconds.getValue() + (seizureDurationMinutes.getValue() * 60));
        String height = String.valueOf(heightInches.getValue() + (heightFeet.getValue() * 12));
        String weight = weightInput.getText().toString().trim();
        String seizureFrequencyPerMonth = seizureFrequency.getText().toString().trim();
        String seizureT = seizureType.getSelectedItem().toString().trim();
        String sex = sexInput.getSelectedItem().toString().trim();

        //checks to see if any inputs are empty and alerts user.
        if (seizureDuration.equals("0")) {
            seizureDurationSeconds.requestFocus();
            return;
        }
        
        if (height.equals("0")) {
            heightFeet.requestFocus();
            return;
        }

        if (weight.equals("0")) {
            weightInput.requestFocus();
            return;
        }

        if (seizureT.equals("0")) {
            seizureType.requestFocus();
            return;
        }

        if (seizureStartD == "") {
            openDatePicker.requestFocus();
            openDatePicker.setError("A seizure start date is required!");
            return;
        }

        // grab data from last questionnaire
        localSettings.setSeizureDuration(seizureDuration);
        localSettings.setHeight(height);
        localSettings.setWeight(weight);
        localSettings.setSeizureFrequency(seizureFrequencyPerMonth);
        questionnaireComplete();
        
    }

    private void questionnaireComplete(){
        localSettings.setQuestionnaireComplete("1");

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(LocalSettings.DEFAULT, localSettings.getSeizureDuration());
        editor.putString(LocalSettings.DEFAULT, localSettings.getHeight());
        editor.putString(LocalSettings.DEFAULT, localSettings.getWeight());
        editor.putString(LocalSettings.DEFAULT, localSettings.getSeizureFrequency());
        editor.putString(LocalSettings.DEFAULT, localSettings.getQuestionnaireComplete());
        editor.apply();

        Log.d("Local Storage", "" + localSettings.getCountdownTimer());
        startActivity(new Intent(QuestionnaireMedical.this, Navbar.class));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        seizureStartD = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}