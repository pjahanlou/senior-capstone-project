package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionnairePersonal extends AppCompatActivity implements View.OnClickListener, Serializable {
    public EditText nameInput, countdownTimerInput, ageInput;
    public Button submitQuestionnaireButton, addContactButton;
    public Spinner contactMethodSpinner;
    public FirebaseAuth mAuth;

    //constructs and instance of an object containing the questionnaire data
    public Questionnaire contactListObject = new Questionnaire
            (
                    "",
                    addedContacts,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_personal);

        //firebase DB
        mAuth = FirebaseAuth.getInstance();

        // Get the UI elements
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        contactMethodSpinner = findViewById(R.id.contactPreferenceSpinner);
        countdownTimerInput = findViewById(R.id.countdownTimerInput);
        addContactButton = findViewById(R.id.addContact);
        submitQuestionnaireButton = findViewById(R.id.submitQuestionairePersonal);

        // Add click listeners to buttons
        addContactButton.setOnClickListener(this);
        submitQuestionnaireButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
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
        String age = ageInput.getText().toString().trim();
        String contactMethod = contactMethodSpinner.getSelectedItem().toString().trim();
        String countdownTimer = countdownTimerInput.getText().toString().trim();

        //checks to see if any inputs are empty and alerts user.
        if (name.isEmpty()) {
            nameInput.setError("Contact method is required!");
            nameInput.requestFocus();
            return;
        }

        if (age.isEmpty()) {
            ageInput.setError("Age is required!");
            ageInput.requestFocus();
            return;
        }

        if (countdownTimer.isEmpty()) {
            countdownTimerInput.setError("Countdown timer is required!");
            countdownTimerInput.requestFocus();
            return;
        }

        //Store Data in Questionnaire class object
        contactListObject.name = name;
        contactListObject.age = age;
        contactListObject.addedContacts = addedContacts;
        contactListObject.contactMethod = contactMethod;
        contactListObject.countdownTimer = countdownTimer;

        Intent i = new Intent(this, QuestionnaireMedical.class);
        i.putExtra("contactListObject", contactListObject);
        startActivity(i);
    }
}