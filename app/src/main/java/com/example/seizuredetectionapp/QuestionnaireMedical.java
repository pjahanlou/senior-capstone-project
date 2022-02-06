package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import java.util.Calendar;


public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener{
    NumberPicker seizureDurationMinutes, seizureDurationSeconds, heightFeet, heightInches;
    EditText seizureFrequency, seizureStartDate, seizureStartYear, weightInput;
    Spinner seizureType, sexInput, seizureStartMonth;
    Button submitQuestionnaireMedical;


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

        //seizureStart = findViewById(R.id.seizureStartButton);
        submitQuestionnaireMedical = findViewById(R.id.submitQuestionnaireMedical);

        seizureDurationMinutes.setMinValue(0);
        seizureDurationMinutes.setMaxValue(60);

        seizureDurationSeconds.setMinValue(0);
        seizureDurationSeconds.setMaxValue(59);

        heightFeet.setMinValue(0);
        heightFeet.setMaxValue(12);

        heightInches.setMinValue(0);
        heightInches.setMaxValue(11);

        //seizureStart.setOnClickListener(this);
        submitQuestionnaireMedical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.seizureStartButton:
                showDatePickerDialog();
                break;*/

            case R.id.submitQuestionnaireMedical: {
                int seizureDuration = seizureDurationSeconds.getValue() + (seizureDurationMinutes.getValue() * 60);
                int height = heightInches.getValue() + (heightFeet.getValue() * 12);
                int weight = Integer.parseInt(weightInput.getText().toString());
                int seizureFrequencyPerMonth = Integer.parseInt(seizureFrequency.getText().toString());
                int seizureStartD = Integer.parseInt(seizureStartDate.getText().toString());
                String seizureStartM = seizureStartMonth.getSelectedItem().toString().trim();
                int seizureStartY = Integer.parseInt(seizureStartYear.getText().toString());
                String seizureT = seizureType.getSelectedItem().toString().trim();
                String sex = sexInput.getSelectedItem().toString().trim();
                /*
                Calendar seizureStart = Calendar.getInstance();
                int year = seizureStart.get(Calendar.YEAR);
                int month = seizureStart.get(Calendar.MONTH);
                int day = seizureStart.get(Calendar.DAY_OF_MONTH);
                */

                //checks to see if any inputs are empty and alerts user.
                if (height == 0) {
                    //heightFeet.setError("please set a valid height");
                    heightFeet.requestFocus();
                    return;
                }

                if (weight == 0) {
                    //weight.setError("Age is required!");
                    weightInput.requestFocus();
                    return;
                }
                // grab data from last questionnaire
                Intent i = getIntent();
                Questionnaire personalObject = (Questionnaire)i.getSerializableExtra("contactListObject");
                personalObject.seizureDuration = seizureDuration;
                personalObject.height = height;
                personalObject.weight = weight;
                personalObject.seizureFrequencyPerMonth = seizureFrequencyPerMonth;
                personalObject.seizureStartD = seizureStartD;
                personalObject.seizureStartM = seizureStartM;
                personalObject.seizureStartY = seizureStartY;
                personalObject.seizureT = seizureT;
                personalObject.sex = sex;


                // Update the database
                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(currentUserUID).child("Settings");

                myRef.setValue(personalObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuestionnaireMedical.this, "Questionnaire saved.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(QuestionnaireMedical.this, Datatable.class));
                        }
                        else {
                            Toast.makeText(QuestionnaireMedical.this, "Questionnaire save failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
/*
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePickerDialog.OnDateSetListener) this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }*/
}