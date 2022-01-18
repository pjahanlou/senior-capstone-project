package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PopUpWindow extends Activity implements View.OnClickListener{
    public TextView allContacts;
    public EditText nameInput, countdownTimerInput, ageInput, emergencyContactInput;
    public Button submitQuestionnaireButton, addContactButton;
    public ArrayList<String> contactList = new ArrayList<>();
    public Spinner contactMethodSpinner;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);

        //firebase DB
        mAuth = FirebaseAuth.getInstance();

        // Get the UI elements
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        contactMethodSpinner = findViewById(R.id.contactPreferenceSpinner);
        countdownTimerInput = findViewById(R.id.countdownTimerInput);
        emergencyContactInput = findViewById(R.id.emergencyContactInput);
        allContacts = findViewById(R.id.allContacts);
        addContactButton = findViewById(R.id.addContact);
        submitQuestionnaireButton = findViewById(R.id.submitQuestionaire);

        // Add click listeners to buttons
        addContactButton.setOnClickListener(this);
        submitQuestionnaireButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.addContact:
                String contact = emergencyContactInput.getText().toString().trim();

                // Adding contact to contactList array
                contactList.add(contact);

                // Store contact in the bigger Text box
                allContacts.setText(contact);
                break;

            case R.id.submitQuestionaire:
                storeQuestionnaireData();
                break;
        }
    }

    private void storeQuestionnaireData() {
        String name = nameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();
        String contactMethod = contactMethodSpinner.getSelectedItem().toString().trim();
        String countdownTimer = countdownTimerInput.getText().toString().trim();
        String contacts = contactList.toString();

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

        if (contacts.isEmpty()) {
            emergencyContactInput.setError("An emergency contact is required!");
            emergencyContactInput.requestFocus();
            return;
        }

        Questionnaire contactListObject = new Questionnaire(name, contactList, countdownTimer, age, contactMethod);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Questionnaire");

        myRef.push().setValue(contactListObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(PopUpWindow.this, Datatable.class));
                    Toast.makeText(PopUpWindow.this, "Questionnaire saved.", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(PopUpWindow.this, PopUpWindow.class));
                    Toast.makeText(PopUpWindow.this, "Questionnaire save failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}