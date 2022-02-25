package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class QuestionnairePersonal extends AppCompatActivity implements View.OnClickListener, Serializable, DatePickerDialog.OnDateSetListener {
    public EditText nameInput, countdownTimerInput;
    public Button submitQuestionnaireButton, addContactButton, dateOfBirth;
    public PowerSpinnerView contactMethodSpinner;
    public FirebaseAuth mAuth;
    public String selectedDOB;
    public LocalSettings localSettings;
    public Set<String> listOfContacts = new HashSet<>();
    public String contactMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_personal);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //firebase DB
        mAuth = FirebaseAuth.getInstance();

        // Get the UI elements
        nameInput = findViewById(R.id.nameInput);
        dateOfBirth = findViewById(R.id.dateOfBirthInput);
        contactMethodSpinner = findViewById(R.id.contactPreferenceSpinner);
        countdownTimerInput = findViewById(R.id.countdownTimerInput);
        addContactButton = findViewById(R.id.addContact);
        submitQuestionnaireButton = findViewById(R.id.submitQuestionairePersonal);

        // Add click listeners to buttons
        dateOfBirth.setOnClickListener(this);
        addContactButton.setOnClickListener(this);
        submitQuestionnaireButton.setOnClickListener(this);

        contactMethodSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                contactMethod = newItem;
            }
        });

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.dateOfBirthInput:
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

            case R.id.addContact:
                Intent intent = new Intent(this, ContactsPage.class);
                startActivity(intent);
                break;

            case R.id.submitQuestionairePersonal:
                storeQuestionnaireData();
                break;
        }
    }

    private void storeQuestionnaireData() {
        Log.d("confirmation", "completed list: " + addedContacts);
        String name = nameInput.getText().toString().trim();
        String countdownTimer = countdownTimerInput.getText().toString().trim();


        //checks to see if any inputs are empty and alerts user.
        if (name.isEmpty()) {
            nameInput.setError("Contact method is required!");
            nameInput.requestFocus();
            return;
        }

        /*if (selectedDOB.equals(null)) {
            dateOfBirth.setError("Age is required!");
            dateOfBirth.requestFocus();
            return;
        }*/

        if (countdownTimer.isEmpty()) {
            countdownTimerInput.setError("Countdown timer is required!");
            countdownTimerInput.requestFocus();
            return;
        }

        //Log.d("added contacts test", "" + addedContacts.toString());

        questionnaireComplete("name", name);
        questionnaireComplete("countdownTimer", countdownTimer);
        questionnaireComplete("age", selectedDOB);
        questionnaireComplete("preferred contact method", contactMethod);

        Log.d("countdown timer Qp", "" + countdownTimer);

        localSettings.setContactList(addedContacts);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("contact method", localSettings.getContactList());
        editor.apply();

        Intent i = new Intent(this, QuestionnaireMedical.class);
        startActivity(i);
    }

    private void questionnaireComplete(String field, String value){
        localSettings.setField(field, value);

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(LocalSettings.DEFAULT, localSettings.getField(field));
        editor.apply();
    }
    
    @Override
    public void onDateSet(DatePicker datePicker,  int year, int month, int dayOfMonth) {
        selectedDOB = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}