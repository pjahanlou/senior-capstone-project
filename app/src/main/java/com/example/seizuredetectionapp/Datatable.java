package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Datatable extends AppCompatActivity {
    Button btnAddJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);

        //create button
        btnAddJournal = (Button) findViewById(R.id.btnjournaladd);
        //set listener to wait for button click to open pop up window
        btnAddJournal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //opens up journal activity on button press
                startActivity(new Intent(Datatable.this, AddJournal.class));
            }
        });
    }



}
