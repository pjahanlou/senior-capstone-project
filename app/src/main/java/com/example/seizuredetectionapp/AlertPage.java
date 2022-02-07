package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AlertPage extends AppCompatActivity implements View.OnClickListener{

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private static TimerStatus timerStatus;
    private static long timeCountInMilliSeconds;

    public static String userCountdownTime = "30";
    public static String preferredContactMethod;
    public static ArrayList<String> contactList;
    private String seizureMessage = "Help! I'm having a seizure!";
    private String cancelMessage = "Get Punked! I didn't have a seizure";

    private SmsManager smsManager;

    private static Button callButton, cancelButton;
    private static TextView timeTextView, infoText;
    private static ProgressBar counterProgressBar;
    private static CountDownTimer countDownTimer;
    private static RippleBackground rippleBackground;
    private static Context context;
    private static String helpRequestSent = "Help request sent. Waiting for acknowledgement";
    private static String helpRequestInitiated = "Help request has been initiated. Help request will be sent in";

    public FirebaseDatabase database;
    public static DatabaseReference userTable;
    private static String currentUserUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_page);

        // Get SMS permissions
        // TODO: move to questionnaire page in the future
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        // database configurations
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Users").child(currentUserUID);

        // Initializing the views
        callButton = findViewById(R.id.callnow);
        cancelButton = findViewById(R.id.cancel);
        timeTextView = findViewById(R.id.textViewTime);
        infoText = findViewById(R.id.infoText);
        counterProgressBar = findViewById(R.id.progressBarCircle);
        rippleBackground = findViewById(R.id.ripple);
        context = getApplicationContext();

        // Add event listeners to the buttons
        callButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // Initializing the sms manager
        smsManager = SmsManager.getDefault();

        // Set necessary data and start the countdown timer
        timerStatus = TimerStatus.STARTED;
        startAlertPage();

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.callnow:
                alertContactList(preferredContactMethod, contactList, seizureMessage);
                stopCountDownTimer();
                changeUI(timerStatus);
                saveJournal();
                break;

            case R.id.cancel:
                if(timerStatus == TimerStatus.STARTED){
                    startActivity(new Intent(AlertPage.this, Datatable.class));
                    stopCountDownTimer();
                }
                if(timerStatus == TimerStatus.STOPPED){
                    startActivity(new Intent(AlertPage.this, Datatable.class));
                    alertContactList(preferredContactMethod, contactList, cancelMessage);
                    timerStatus = TimerStatus.STARTED;
                    changeUI(timerStatus);
                }
                break;
        }
    }

    /**
     * method to stop the countdown timer and update
     * the timer status
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
        timerStatus = TimerStatus.STOPPED;
    }

    /**
     * method to retrieve settings info fro user table and
     * updated the appropriate class variables
     *
     * This method also starts the countdown timer. Since the
     * countdown timer relies on the data retrieved from firebase.
     */
    private void startAlertPage() {

        userTable.child("Settings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Questionnaire settings = snapshot.getValue(Questionnaire.class);
                AlertPage.preferredContactMethod = settings.contactMethod;
                AlertPage.contactList = settings.addedContacts;
                AlertPage.userCountdownTime = settings.countdownTimer;

                AlertPage.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Setting Data Retrieval", error.getDetails());
            }
        });

    }

    /**
     * helper method to allow startAlertPage() to initialize the
     * countdown timer
     */
    private static void start() {
        setTimerValues();
        startCountDownTimer();
    }

    /**
     * method to contact user contacts based on their
     * preferences
     *
     * TODO: Add the other ways of contacting
     * TODO: Add their location in message
     */
    private void alertContactList(String contactMethod, ArrayList<String> contactList, String message) {
        switch (contactMethod){
            case "text message":

                /*for(String contactPerson:contactList){
                    Set<String> names = contactPerson.keySet();
                    String name = names.iterator().next();
                    smsManager.sendTextMessage(contactPerson.get(name), null, message, null, null);
                }*/
                break;

            case "email":
                break;

            case "call":
                break;
        }
    }

    /**
     * method to initialize the values for count down timer
     *
     * reads from firebase and assigns the value to timeCountInMilliSeconds
     */
    public static void setTimerValues() {
        long countdownTime = Integer.parseInt(userCountdownTime);

        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = countdownTime * 1000;
    }

    /**
     * method to start count down timer
     */
    public static void startCountDownTimer() {

        setProgressBarValues();
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 50) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeTextView.setText(millisUntilFinished/1000 + " Sec");
                counterProgressBar.setProgress((int) (millisUntilFinished / 50));

                // if countdown timer under 10 secs, change text color
                if(millisUntilFinished/1000 < 10){
                    timeTextView.setTextColor(Color.parseColor("#FF0000"));
                }

            }

            @Override
            public void onFinish() {
                // save journal information to firebase
                saveJournal();

                // update the ui and timer status
                timerStatus = TimerStatus.STOPPED;
                changeUI(timerStatus);
            }

        }.start();
    }

    /**
     * method to push a new journal to firebase when
     * a seizure has occurred
     *
     * TODO: Update the journal info in the future
     */
    private static void saveJournal(){
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").
                format(Calendar.getInstance().getTime());
        String moodType = "";
        String seizureType = "";
        String durationOfSeizure = "";
        String seizureTrigger = "";
        String seizureDescription = "";
        String postSeizureDescription = "";

        Journal newJournal = new Journal(timeStamp, moodType, seizureType, durationOfSeizure,
                seizureTrigger, seizureDescription, postSeizureDescription);

        userTable.child("Journals").push().setValue(newJournal)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Journal saved", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(context, "Journal error!", Toast.LENGTH_LONG).show();
                        Log.d("Journal Error: ", task.getException().getLocalizedMessage());
                    }
                });
    }

    /**
     * method to update the UI based on the status of
     * the countdown timer
     */
    private static void changeUI(TimerStatus timerStatus) {
        switch(timerStatus){
            case STARTED:
                infoText.setText(helpRequestInitiated);
                cancelButton.setText("Cancel Countdown");

                callButton.setVisibility(View.VISIBLE);
                timeTextView.setVisibility(View.VISIBLE);
                counterProgressBar.setVisibility(View.VISIBLE);

                rippleBackground.stopRippleAnimation();
                rippleBackground.setVisibility(View.INVISIBLE);

            case STOPPED:
                infoText.setText(helpRequestSent);
                cancelButton.setText("Cancel Help");

                callButton.setVisibility(View.INVISIBLE);
                timeTextView.setVisibility(View.INVISIBLE);
                counterProgressBar.setVisibility(View.INVISIBLE);

                rippleBackground.setVisibility(View.VISIBLE);
                rippleBackground.startRippleAnimation();
        }
    }

    /**
     * method to set circular progress bar values
     */
    private static void setProgressBarValues() {

        counterProgressBar.setMax((int) timeCountInMilliSeconds / 50);
        counterProgressBar.setProgress((int) timeCountInMilliSeconds / 1000);
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private static String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }
}