package com.example.seizuredetectionapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddJournal extends Activity implements View.OnClickListener {
    //class variables
    private EditText  description, postDescription;
    private static NachoTextView triggers, mood, typeOfSeizure;
    Button btnClose, btnSave;
    Boolean edit;
    private String ID;
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
    private static RangeSlider duration;
    private Button dateAndTimePicker;
    private ImageView seizureHint;
    private int hour, minute, year, month, day;
    private int durHour = 0, durMinute = 0, durSecond = 0;
    private Calendar cal, cal1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addjournal);

        //firebase DB
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Users").child(currentUserUID);

        //get ui elements
        dateAndTimePicker = findViewById(R.id.datetime);
        mood = findViewById(R.id.mood);
        typeOfSeizure = findViewById(R.id.typeofseizure);
        duration = findViewById(R.id.duration);
        triggers = findViewById(R.id.triggers);
        description = findViewById(R.id.description);
        postDescription = findViewById(R.id.postdescription);
        btnSave =  findViewById(R.id.btnsave);
        btnClose =  findViewById(R.id.btnclose);
        severitySlider = findViewById(R.id.severitySlider);
        seizureHint = findViewById(R.id.seizureInfo);
        //hintImage = findViewById(R.id.hintAddJournal);

        //get calendar
        cal = Calendar.getInstance();
        cal1 = Calendar.getInstance();

        //if user pressed edit
        Bundle extras = getIntent().getExtras();
        edit = false;
        if(extras != null){
            edit = extras.getBoolean("key");
            ID = extras.getString("id");
        }
        if(edit){
            //Retrieving saved journal information and populating the EditText
            popJournalText();
        }
        else{
            //auto fill date and time to the current date and time
            dateAndTimePicker.setText(getCurrentTime());
        }
        //onClick Listeners
        btnClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        duration.setOnClickListener(this);
        seizureHint.setOnClickListener(this);
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
                "Hormones" };

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
                ,"Tonic"
                ,"Clonic"
                ,"Absence"
                ,"Myoclonic"
                ,"Atonic"
                ,"Infantile or Epileptic spasms"};

        ArrayAdapter<String> adapterMood = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,moodSuggestions);
        ArrayAdapter<String> adapterTriggers = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,triggerSuggestions);
        ArrayAdapter<String> adapterTypeOfSeizure = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,seizureSuggestions);
        triggers.setAdapter(adapterTriggers);
        typeOfSeizure.setAdapter(adapterTypeOfSeizure);
        mood.setAdapter(adapterMood);

        duration.setLabelFormatter(value -> {
            return durationSeizureConvert(value);
        });

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnclose:
                startActivity(new Intent(AddJournal.this,Navbar.class));
                break;
            case R.id.btnsave:
                if(edit){
                    updateInformation();
                } else {
                    saveInformation();
                }
                startActivity(new Intent(AddJournal.this, Navbar.class));
                break;
            case R.id.duration:
                durationPicker();
                break;
            case R.id.seizureInfo:
                Intent intent = new Intent(AddJournal.this, SeizureMoreInfo.class);
                startActivity(intent);
                break;
        }
    }

    //method for retrieving info written and saving to firebase
    public String saveInformation()
    {
        List<String> saveTriggers = new ArrayList<String>();
        List<String> saveMood = new ArrayList<String>();
        List<String> saveTypeOfSeizure = new ArrayList<String>();
        //retrieving text from text boxes
        String saveDateAndTime = dateAndTimePicker.getText().toString().trim();
        saveMood = mood.getChipValues();
        saveTypeOfSeizure = typeOfSeizure.getChipValues();
        //String saveDuration = duration.getText().toString().trim();
        String saveDuration = duration.getValues().get(0).toString();
        saveTriggers = triggers.getChipValues();
        String saveDescription = description.getText().toString().trim();
        String savePostDescription = postDescription.getText().toString().trim();
        String saveSeverity = severitySlider.getValues().get(0).toString();

        if(saveDateAndTime.isEmpty()){
            dateAndTimePicker.requestFocus();
            Toast.makeText(AddJournal.this, "Date and Time field was empty. Please fill out the Date and Time Field", Toast.LENGTH_LONG).show();
            return "Failed";
        }

        if(saveDescription.isEmpty()){
            saveDescription = "No description was put.";
        }

        if(saveDuration.isEmpty()){
            saveDuration = "No duration recorded";
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
                if (task.isSuccessful()){
                    Toast.makeText(AddJournal.this, "Journal Saved.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(AddJournal.this, "Journal Save Failed.", Toast.LENGTH_LONG).show();
                }
            }
        });
        String id = journal.getDateAndTime();
        return id;
    }

    public void updateInformation(){

        //Retrieving new inputted information
        String dateTime = dateAndTimePicker.getText().toString().trim();
        List<String> moodType = mood.getChipValues();
        List<String> seizureType = typeOfSeizure.getChipValues();
        //durationOfSeizure = duration.getText().toString().trim();
        String durationOfSeizure = duration.getValues().get(0).toString();
        List<String> seizureTrigger = triggers.getChipValues();
        String seizureDescription = description.getText().toString().trim();
        String postSeizureDescription = postDescription.getText().toString().trim();
        String severity = severitySlider.getValues().get(0).toString();

        if(dateTime.isEmpty()){
            dateAndTimePicker.requestFocus();
            Toast.makeText(AddJournal.this, "Date and Time field was empty. Did not save changes.", Toast.LENGTH_LONG).show();
            return;
        }

        updateFieldInFirebase("dateAndTime", dateTime, editJournal.dateAndTime);
        updateListFieldInFirebase("mood",moodType, editJournal.mood);
        updateListFieldInFirebase("typeOfSeizure",seizureType, editJournal.typeOfSeizure);
        updateFieldInFirebase("durationOfSeizure",durationOfSeizure, editJournal.durationOfSeizure);
        updateListFieldInFirebase("triggers",seizureTrigger,editJournal.triggers);
        updateFieldInFirebase("description",seizureDescription, editJournal.description);
        updateFieldInFirebase("postDescription", postSeizureDescription, editJournal.postDescription);
        updateFieldInFirebase("severity",severity, editJournal.severity);
        Toast.makeText(AddJournal.this,"Journal edited and saved.",Toast.LENGTH_SHORT).show();

    }
    public void popJournalText(){
        //set existing journal entries to each edittext
        Log.d("1", ID);
        userTable.child("Journals").orderByChild("dateAndTime").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
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
                    float fSeverity = Float.parseFloat(AddJournal.updateSeverity);
                    float fDuration = Float.parseFloat(AddJournal.updateDuration);
                    //Set EditText to existing saved values
                    dateAndTimePicker.setText(AddJournal.updateDateTime);
                    AddJournal.mood.setText(updateMood);
                    AddJournal.typeOfSeizure.setText(updateTypeOfSeizure);
                    //AddJournal.duration.setText(updateDuration);
                    AddJournal.duration.setValues(fDuration);
                    AddJournal.triggers.setText(updateTriggers);
                    description.setText(updateDescription);
                    postDescription.setText(updatePostDescription);
                    severitySlider.setValues(fSeverity);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Setting Data Retrieval", error.getDetails());
            }
        });
    }

    private void updateFieldInFirebase(String field, String newValue, String previousValue){
        if(previousValue != null) {
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

    private String getCurrentTime(){
        //gets current time and date
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.addjournal_datepicker_theme, onDateSetListener, year, day, month);
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
                dateAndTimePicker.setText(String.format(Locale.getDefault(), "%02d/%02d/%02d %02d:%02d", month + 1, day, year,hour,minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.addjournal_datepicker_theme, onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }

    /**
     * durationPicker handles the duration number picker and displays the duration accordingly
     */
    public void durationPicker(){

        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, 0, new MyTimePickerDialog.OnTimeSetListener() {
            String dHour = "";
            String dMinute = "";
            String dSecond = "";
            String durationTotal = "";
            @Override
            public void onTimeSet(com.ikovac.timepickerwithseconds.TimePicker view, int hourOfDay, int minute, int seconds) {
                durHour = hourOfDay;
                durMinute = minute;
                durSecond = seconds;
                if(String.valueOf(durHour).length() == 1){
                    dHour = "0"+durHour;
                    Log.d("dhour", dHour);
                } else{
                    dHour = String.valueOf(durHour);
                }
                if(String.valueOf(durMinute).length() == 1){
                    dMinute = "0"+durMinute;
                    Log.d("dhour", dMinute);
                } else{
                    dMinute = String.valueOf(durMinute);
                }
                if(String.valueOf(durSecond).length() == 1){
                    dSecond = "0"+durSecond;
                    Log.d("dhour", dSecond);
                } else{
                    dSecond = String.valueOf(durSecond);
                }
                //duration.setText(dHour + dMinute + dSecond);
                durationTotal = dHour+":"+dMinute+":"+dSecond;
                //duration.setText(durationTotal);
            }
        }, 0, 0, 0, true);
        //mTimePicker.findViewById(Resources.getSystem().getIdentifier("hourOfDay","id","android")).setVisibility(View.GONE);
        mTimePicker.show();
    }

    private String durationSeizureConvert(float value) {
        if(value == 0){
            return "30 Sec";
        } else if(value == 120) {
            return "1 Hour";
        }else if(value == 1){
                return ((int)value)+" Min";
        } else if(value % 2 == 1){
            return ((int)value/2)+" Min 30 Sec";
        }  else{
            return ((int)value/2)+" Min";
        }
    }

}
