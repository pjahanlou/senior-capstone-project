package com.example.seizuredetectionapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datatable extends AppCompatActivity {
    Button btnAddJournal, btnSettings;
    ListView journalList;
    ArrayList<String> journalInfo = new ArrayList<>();
    ArrayList<String> journalMap = new ArrayList<>();
    static ArrayAdapter adapter;
    Journal journal;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);

        //firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Journal");

        //ui elements
        btnAddJournal = (Button) findViewById(R.id.btnjournaladd);
        btnSettings = findViewById(R.id.settings);
        journalList = (ListView) findViewById(R.id.displayJournal);

        //adapter for listview
        adapter = new ArrayAdapter<>(this, R.layout.listview_textformat, journalInfo);
        journalList.setAdapter(adapter);

        //populates the listView with information from Firebase
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //retrieves the hashmap of journal entry from firebase and displays the date and time
                GenericTypeIndicator<HashMap<String,String>> t = new GenericTypeIndicator<HashMap<String,String>>() {};
                HashMap<String,String> messages = snapshot.getValue(t);
                journalInfo.add(messages.get("dateAndTime"));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //button functionality to change to addJournal activity
        btnAddJournal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //opens up journal activity on button press
                Intent intent = new Intent(Datatable.this, AddJournal.class);
                startActivity(intent);

            }
        });

        //button functionality to change to addJournal activity
        btnSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //opens up journal activity on button press
                Intent intent = new Intent(Datatable.this, AlertPage.class);
                startActivity(intent);

            }
        });

    }

}
