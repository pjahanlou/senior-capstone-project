package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Datatable extends AppCompatActivity {
    Button btnAddJournal, btnSettings;
    ListView journalList;
    ArrayList<String> journalInfo = new ArrayList<>();
    static ArrayAdapter adapter;
    Journal journal;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout sheetBottom;
    private String currentUserUID;
    BottomSheetBehavior bottomSheetBehavior;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);

        //firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(currentUserUID);

        //ui elements
        btnAddJournal = (Button) findViewById(R.id.btnjournaladd);
        btnSettings = findViewById(R.id.settings);
        journalList = (ListView) findViewById(R.id.journalList);

        //listview set up
        adapter = new ArrayAdapter<>(this, R.layout.listview_textformat, journalInfo);
        journalList.setAdapter(adapter);

        //Bottom Swipe up setup
        sheetBottom = findViewById(R.id.bottom_sheet_header);
        bottomSheetBehavior = BottomSheetBehavior.from(sheetBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //Peek Height
        bottomSheetBehavior.setPeekHeight(210);

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

        //Populate ListView
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Journal journal  = snapshot.getValue(Journal.class);
                journalInfo.add(journal.dateAndTime);
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

        //click row, brings up edit or remove
        journalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //Dialog popup for choosing edit or remove journal
                AlertDialog.Builder editOrRemove = new AlertDialog.Builder(Datatable.this);
                editOrRemove.setTitle("Do you want to edit or remove this journal?");
                editOrRemove.setMessage("Edit or Remove?");
                editOrRemove.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(Datatable.this, "Edited", Toast.LENGTH_SHORT).show();

                    }
                });

                editOrRemove.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        //Confirmation on removing journal
                        AlertDialog.Builder confirmRemove = new AlertDialog.Builder(Datatable.this);
                        confirmRemove.setTitle("Are you sure you want to remove this journal?");
                        confirmRemove.setMessage("Yes or No");
                        confirmRemove.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeJournal(pos);
                                Toast.makeText(Datatable.this, "Removed.", Toast.LENGTH_SHORT).show();

                            }
                        });
                        confirmRemove.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Datatable.this, "Canceled.", Toast.LENGTH_SHORT).show();

                            }
                        });
                           confirmRemove.show();

                    }
                });
                editOrRemove.show();

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

        //button functionality to change to AlertPage activity
        btnSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //opens up journal activity on button press
                Intent intent = new Intent(Datatable.this, AlertPage.class);
                startActivity(intent);

            }
        });

    }

    public void removeJournal(int pos){
        //remove single journal from firebase, TODO
        //myRef.child("Journals").orderByChild("dateAndTime").equals(journalInfo.get(pos))
        journalInfo.remove(pos);
        adapter.notifyDataSetChanged();
    }


}
