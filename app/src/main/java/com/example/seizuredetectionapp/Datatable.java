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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    Button btnOpenJournalView;
    FrameLayout sheetBottom;


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


        //Bottom Swipe
        sheetBottom = findViewById(R.id.sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(sheetBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //Peek Height
        bottomSheetBehavior.setPeekHeight(250);
        //Hideable
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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
