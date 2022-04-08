package com.example.seizuredetectionapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hootsuite.nachos.NachoTextView;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cucumber.api.java.cs.A;

public class AddJournal extends Activity implements View.OnClickListener {
    //class variables
    private static EditText  description, postDescription;
    private static NachoTextView triggers, mood, typeOfSeizure;
    Button btnClose, btnSave;
    Journal journal;
    private FirebaseAuth mAuth;
    Boolean edit;
    String ID;
    DatabaseReference myRef;
    public static FirebaseDatabase database;
    public static DatabaseReference userTable;
    private static String currentUserUID;
    public static String updateDateTime;
    public static List<String> updateMood;
    public static List<String> updateTypeOfSeizure;
    public static String updateDuration;
    public static List<String> updateTriggers;
    public static String updateDescription;
    public static String updatePostDescription;
    public static String updateSeverity;
    private Journal editJournal;
    private String journalKey;
    private RangeSlider severitySlider;
    private static Button dateAndTime, duration;
    private int hour, minute, year, month, day;
    private int durHour = 0, durMinute = 0, durSecond = 0;
    private Calendar cal, cal1, now;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addjournal);

        //firebase DB
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Users").child(currentUserUID);

        //get ui elements
        dateAndTime = findViewById(R.id.datetime);
        mood = findViewById(R.id.mood);
        typeOfSeizure = findViewById(R.id.typeofseizure);
        duration = findViewById(R.id.duration);
        triggers = findViewById(R.id.triggers);
        description = findViewById(R.id.description);
        postDescription = findViewById(R.id.postdescription);
        btnSave = findViewById(R.id.btnsave);
        btnClose = findViewById(R.id.btnclose);
        severitySlider = findViewById(R.id.severitySlider);
        //hintImage = findViewById(R.id.hintAddJournal);

        //get calendar
        cal = Calendar.getInstance();
        cal1 = Calendar.getInstance();
        now = Calendar.getInstance();

        //if user pressed edit
        Bundle extras = getIntent().getExtras();
        edit = false;
        if (extras != null) {
            edit = extras.getBoolean("key");
            ID = extras.getString("id");
            Log.d("journal ID", "id" + ID);
            Log.d("edit boolean", "" + edit.toString());
        }
        Log.d("edit boolean 2", "" + edit.toString());
        if (edit) {
            //Retrieving saved journal information and populating the EditText
            popJournalText();
        } else {
            //auto fill date and time to the current date and time
            AddJournal.dateAndTime.setText(getCurrentTime());
        }
        //onClick Listeners
        btnClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        duration.setOnClickListener(this);
        //btnDate.setOnClickListener(this);

        //Triggers suggestions
        String[] triggerSuggestions = new String[]{"Stress", "Missed Medication",
                "Caffeine",
                "Anxiety",
                "Recreational Drugs",
                "Alcohol",
                "Lack of Sleep",
                "Dehydration",
                "Skipped Meal",
                "Flashing Lights",
                "Flickering Lights",
                "Hormones"};

        String[] moodSuggestions = new String[]{"Happy", "Sad", "Angry",
                "Depressed",
                "Cheerful",
                "Romantic",
                "Irritable",
                "Calm",
                "Fearful",
                "Tense",
                "Lonely",
                "Lighthearted",
                "Humorous"};

        String[] seizureSuggestions = new String[]{"Generalized tonic-clonic (GTC)"
                , "Tonic"
                , "Clonic"
                , "Absence"
                , "Myoclonic"
                , "Atonic"
                , "Infantile or Epileptic spasms"};

        ArrayAdapter<String> adapterMood = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodSuggestions);
        ArrayAdapter<String> adapterTriggers = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, triggerSuggestions);
        ArrayAdapter<String> adapterTypeOfSeizure = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, seizureSuggestions);
        triggers.setAdapter(adapterTriggers);
        typeOfSeizure.setAdapter(adapterTypeOfSeizure);
        mood.setAdapter(adapterMood);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnclose:
                finish();
                break;
            case R.id.btnsave:
                if (edit) {
                    updateInformation();
                } else {
                    saveInformation();
                }
                startActivity(new Intent(AddJournal.this, Navbar.class));
                break;
            case R.id.duration:
                durationPicker();
                break;
        }
    }

    /**
     * saveInformation saves new created journals to firebase
     */
    public void saveInformation() {
        List<String> saveTriggers = new ArrayList<String>();
        List<String> saveMood = new ArrayList<String>();
        List<String> saveTypeOfSeizure = new ArrayList<String>();

        //retrieving text from text boxes
        String saveDateAndTime = dateAndTime.getText().toString().trim();
        saveMood = mood.getChipValues();
        saveTypeOfSeizure = typeOfSeizure.getChipValues();
        String saveDuration = duration.getText().toString().trim();
        saveTriggers = triggers.getChipValues();
        String saveDescription = description.getText().toString().trim();
        String savePostDescription = postDescription.getText().toString().trim();
        String saveSeverity = severitySlider.getValues().get(0).toString();
        if (saveDateAndTime.isEmpty()) {
            dateAndTime.requestFocus();
            Toast.makeText(AddJournal.this, "Date and Time field was empty. Journal was not saved.", Toast.LENGTH_LONG).show();
            return;
        }

        if (saveDescription.isEmpty()) {
            saveDescription = "None";
        }

        if (saveDuration.isEmpty()) {
            saveDuration = "0";
        }

        Journal journal = new Journal(saveDateAndTime, saveMood, saveTypeOfSeizure, saveDuration,
                saveTriggers, saveDescription, savePostDescription, saveSeverity);

        // Sends HashMap of entry to Firebase DB
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(currentUserUID).child("Journals");

        myRef.push().setValue(journal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddJournal.this, "Journal Saved.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddJournal.this, "Journal Save Failed.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * updateInformation updates the edited journal into firebase
     */
    public void updateInformation() {

        //Retrieving new inputted information
        String dateTime = dateAndTime.getText().toString().trim();
        List<String> moodType = mood.getChipValues();
        List<String> seizureType = typeOfSeizure.getChipValues();
        String durationOfSeizure = duration.getText().toString().trim();
        List<String> seizureTrigger = triggers.getChipValues();
        String seizureDescription = description.getText().toString().trim();
        String postSeizureDescription = postDescription.getText().toString().trim();
        String severity = severitySlider.getValues().get(0).toString();

        if (dateTime.isEmpty()) {
            dateAndTime.requestFocus();
            Toast.makeText(AddJournal.this, "Date and Time field was empty. Did not save changes.", Toast.LENGTH_LONG).show();
            return;
        }

        updateFieldInFirebase("dateAndTime", dateTime, editJournal.dateAndTime);
        updateListFieldInFirebase("mood", moodType, editJournal.mood);
        updateListFieldInFirebase("typeOfSeizure", seizureType, editJournal.typeOfSeizure);
        updateFieldInFirebase("durationOfSeizure", durationOfSeizure, editJournal.durationOfSeizure);
        updateListFieldInFirebase("triggers", seizureTrigger, editJournal.triggers);
        updateFieldInFirebase("description", seizureDescription, editJournal.description);
        updateFieldInFirebase("postDescription", postSeizureDescription, editJournal.postDescription);
        updateFieldInFirebase("severity", severity, editJournal.severity);

    }

    /**
     * popJournalText populates the text fields, sliders, and chips of the chosen journal to edit
     */
    public void popJournalText() {
        //set existing journal entries to each edittext
        Log.d("1", "made it here");
        userTable.child("Journals").orderByChild("dateAndTime").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("date1", "date1 = " + snapshot.toString());

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    journalKey = childSnapshot.getKey();
                    editJournal = childSnapshot.getValue(Journal.class);
                    Log.d("2", "made it here " + editJournal.toString());

                    //Get values in EditText
                    AddJournal.updateDateTime = editJournal.dateAndTime;
                    AddJournal.updateMood = editJournal.mood;
                    AddJournal.updateTypeOfSeizure = editJournal.typeOfSeizure;
                    AddJournal.updateDuration = editJournal.durationOfSeizure;
                    AddJournal.updateTriggers = editJournal.triggers;
                    AddJournal.updateDescription = editJournal.description;
                    AddJournal.updatePostDescription = editJournal.postDescription;
                    AddJournal.updateSeverity = editJournal.severity;

                    //change duration and severity to float from string
                    float fSev = Float.parseFloat(AddJournal.updateSeverity);
                    //Set EditText to existing saved values
                    AddJournal.dateAndTime.setText(AddJournal.updateDateTime);
                    AddJournal.mood.setText(updateMood);
                    AddJournal.typeOfSeizure.setText(updateTypeOfSeizure);
                    AddJournal.duration.setText(updateDuration);
                    AddJournal.triggers.setText(updateTriggers);
                    AddJournal.description.setText(updateDescription);
                    AddJournal.postDescription.setText(updatePostDescription);
                    //TODO set slider to existing value

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Setting Data Retrieval", error.getDetails());
            }
        });
    }

    /**
     * updateFieldInFirebase handles updating strings to firebase
     */
    private void updateFieldInFirebase(String field, String newValue, String previousValue) {
        if (previousValue != null) {
            if (!previousValue.equals(newValue)) {
                DatabaseReference journalTable = userTable.child("Journals");
                journalTable.child(journalKey).child(field).setValue(newValue).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(field, "Updated");
                    } else {
                        Log.d(field, task.getException().toString());
                    }
                });
            }
        }
    }

    /**
     * updateListFieldInFirebase handles updating string lists to firebase
     */
    private void updateListFieldInFirebase(String field, List<String> newValue, List<String> previousValue) {
        DatabaseReference journalTable = userTable.child("Journals");
        journalTable.child(journalKey).child(field).setValue(newValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(field, "Updated");
            } else {
                Log.d(field, task.getException().toString());
            }
        });
    }


    /**
     * getCurrentTime gets the time when a new journal is created.
     */
    private String getCurrentTime() {
        //gets current time and date
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").
                format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    /**
     * datePicker handles the dialog for calender and time.
     */
    public void datePicker(View view) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                timePicker();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, day, month);
        cal1.add(Calendar.YEAR, -3);
        datePickerDialog.getDatePicker().setMinDate(cal1.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    private void timePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                dateAndTime.setText(String.format(Locale.getDefault(), "%02d/%02d/%02d %02d:%02d", month + 1, day, year,hour,minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, false);
        timePickerDialog.show();
    }

    /**
     * durationPicker handles the duration number picker and displays the duration accordingly
     */
    public void durationPicker(){

        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {
            String dHour = "";
            String dMinute = "";
            String dSecond = "";
            @Override
            public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                durHour = hourOfDay;
                durMinute = minute;
                durSecond = seconds;
                if(durHour != 0){
                    dHour = String.format("%02d Hrs ", durHour);
                }
                if(durMinute != 0){
                    dMinute = String.format("%02d Min ", durMinute);
                }
                if(durSecond != 0){
                    dSecond = String.format("%02d Sec", durSecond);
                }
                duration.setText(dHour + dMinute + dSecond);
            }
        }, 0, 0, 0, true);
        mTimePicker.show();
    }

}
