package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
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


public class PopUpWindow extends Activity {
    private TextView receiveContacts;
    private EditText nameInput, countdownTimerInput, ageInput, emergencyContactInput;
    private Button submit, addContact;
    private ArrayList<String> contactList=new ArrayList<String>();
    private Spinner contactMethodInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);

        // links variables to their respective xml component
        nameInput = (EditText) findViewById(R.id.nameInput);
        emergencyContactInput = (EditText) findViewById(R.id.emergencyContactInput);
        receiveContacts = (TextView) findViewById(R.id.contactView);
        contactMethodInput = (Spinner) findViewById(R.id.contact_Preference_Input);
        ageInput = (EditText) findViewById(R.id.ageInput);
        countdownTimerInput = (EditText) findViewById(R.id.countdownTimerInput);
        addContact = (Button) findViewById(R.id.addContact);
        submit = (Button) findViewById(R.id.submitQuestionaire);

        // used to collect the contact and add it to a list when a button is clicked
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = emergencyContactInput.getText().toString().trim();
                contactList.add(contact);
                receiveContacts.setText(contact);
            }
        });

        // Starts the process of sending data to firebase when a button is clicked
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                writeToFirebase();
            }
        });

    }
    private void writeToFirebase() {
        //Converts the data gathered from the questionnaire into readable text
        String name = nameInput.getText().toString().trim();
        String contactMethod = contactMethodInput.getSelectedItem().toString().trim();
        String age = ageInput.getText().toString().trim();
        String countdownTimer = countdownTimerInput.getText().toString().trim();
        String contacts = contactList.toString().trim();

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
        if (contacts.isEmpty()) {
            emergencyContactInput.setError("An emergency contact is required!");
            emergencyContactInput.requestFocus();
            return;
        }

        //constructs and instance of an object containing the questionnaire data
        Questionnaire contactListObject = new Questionnaire(name, contactList, countdownTimer, age, contactMethod);

        // Writes a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("server/Questionnaire-data").child("Questionnaire");

        myRef.push().setValue(contactListObject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // alerts the user that the questionnaire has properly sent
                    Toast.makeText(PopUpWindow.this, "Questionnaire Saved.", Toast.LENGTH_LONG).show();
                } else {
                    // alerts the user that the questionnaire has not properly sent
                    Toast.makeText(PopUpWindow.this, "Questionnaire Save Failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}