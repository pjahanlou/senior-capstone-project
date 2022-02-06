package com.example.seizuredetectionapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddJournal extends Activity {
    //class variables
    EditText dateAndTime, mood, typeOfSeizure, duration, triggers, description, postDescription;
    Button btnCloseWindow, btnSave;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addjournal);

        //firebase DB
        mAuth = FirebaseAuth.getInstance();

        //get ui elements
        dateAndTime = findViewById(R.id.datetime);
        mood = findViewById(R.id.mood);
        typeOfSeizure = findViewById(R.id.typeofseizure);
        duration = findViewById(R.id.duration);
        triggers = findViewById(R.id.triggers);
        description = findViewById(R.id.description);
        postDescription = findViewById(R.id.postdescription);
        btnSave = (Button) findViewById(R.id.btnsave);
        btnCloseWindow = (Button) findViewById(R.id.btnclose);

        //close button
        btnCloseWindow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //closes activity and returns to datatable
                finish();
            }
        });

        //save button
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //saves information to firebase then closes popup activity
                saveInformation();
                finish();
            }
        });

    }
    //method for retrieving info written and saving to firebase
    public void saveInformation()
    {
        //retrieving text from text boxes
        String datetime = dateAndTime.getText().toString().trim();
        String moodType = mood.getText().toString().trim();
        String seizureType = typeOfSeizure.getText().toString().trim();
        String durationOfSeizure = duration.getText().toString().trim();
        String seizureTrigger = triggers.getText().toString().trim();
        String seizureDescription = description.getText().toString().trim();
        String postSeizureDescription = postDescription.getText().toString().trim();

        Journal journal = new Journal(datetime, moodType, seizureType, durationOfSeizure,
                seizureTrigger, seizureDescription, postSeizureDescription);

        // Sends HashMap of entry to Firebase DB
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(currentUserUID).child("Journals");

        myRef.push().setValue(journal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddJournal.this, "Journal Saved.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(AddJournal.this, "Journal Save Failed.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void fillText(){

    }

}
