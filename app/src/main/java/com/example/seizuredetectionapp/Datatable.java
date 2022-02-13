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
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Datatable extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    Button btnAddJournal, btnSettings;
    ListView journalList;
    ArrayList<String> journalInfo = new ArrayList<>();
    static ArrayAdapter adapter;
    static ArrayAdapter sortedAdapter;
    Journal journal;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout sheetBottom;
    private String currentUserUID;
    BottomSheetBehavior bottomSheetBehavior;
    private Button btnHelpRequest;
    private Spinner sortSpinner;
    private String[] sortOptions = new String[1];
    ListView sortedJournalList;
    ArrayList<String> sortedJournalInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datatable);

        //firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(currentUserUID);


        //ui elements
        btnAddJournal = findViewById(R.id.btnjournaladd);
        btnSettings = findViewById(R.id.settings);
        btnHelpRequest = findViewById(R.id.helpRequest);
        journalList = findViewById(R.id.journalList);
        sortSpinner = findViewById(R.id.sortSpinner);

        //Buttons
        btnAddJournal.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnHelpRequest.setOnClickListener(this);

        //item press listener
        journalList.setOnItemClickListener(this);

        //spinner
        sortSpinner.setOnItemSelectedListener(this);

        // send those journals to listview
        sortedAdapter = new ArrayAdapter<>(Datatable.this, R.layout.listview_textformat, sortedJournalInfo);
        journalList.setAdapter(sortedAdapter);

        //listview set up
        adapter = new ArrayAdapter<>(this, R.layout.listview_textformat, journalInfo);
        journalList.setAdapter(adapter);

        //Bottom Swipe up setup
        sheetBottom = findViewById(R.id.bottom_sheet_header);
        bottomSheetBehavior = BottomSheetBehavior.from(sheetBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //Peek Height
        bottomSheetBehavior.setPeekHeight(210);

        //set journal to not be hideable
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
                Log.d("child added", "child added " + snapshot);
                Journal journal = snapshot.getValue(Journal.class);
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

    }


    //remove single journal from firebase
    public void removeJournal(int pos){

        //gets key id for chosen journal
        Query query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalInfo.get(pos));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Delete Operation", "onCancelled", databaseError.toException());
            }
        });
        journalInfo.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private void sortJournals(){
        String selectedSortOption = sortSpinner.getSelectedItem().toString().trim();
        ArrayList<String> journalInfo = new ArrayList<>();

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
    }

    public void editJournal(int pos){
        //create new AddJournal intent and pass the dateAndTime to the newly created activity
        Intent intent = new Intent(Datatable.this, AddJournal.class);
        intent.putExtra("key", true);
        Query query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalInfo.get(pos));

        intent.putExtra("id", journalInfo.get(pos));
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case(R.id.btnjournaladd):
                intent = new Intent(Datatable.this, AddJournal.class);
                startActivity(intent);
                break;
            case(R.id.settings):
                intent = new Intent(Datatable.this, MainSettings.class);
                startActivity(intent);
                break;
            case(R.id.helpRequest):
                intent = new Intent(Datatable.this, AlertPage.class);
                startActivity(intent);
                break;
        }
    }

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
                editJournal(pos);

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString().trim();
        Log.d("selected Item", "selected Item" + selectedItem);

        // get a list of all the journals in firebase
        //Populate ListView
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Journal journal = snapshot.getValue(Journal.class);
                sortedJournalInfo.add(journal.dateAndTime);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        // sort those journals
        Collections.sort(sortedJournalInfo);
        sortedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



}
