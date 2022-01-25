package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AlertPage extends AppCompatActivity implements View.OnClickListener{

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STARTED;
    private long timeCountInMilliSeconds = 1 * 60000;

    private String preferredContactMethod;
    private ArrayList<String> contactList;
    private String seizureMessage = "Help! I'm having a seizure!";
    private String cancelMessage = "Get Punked! I didn't have a seizure";

    private SmsManager smsManager;

    private Button callButton, cancelButton;
    private TextView timeTextView;
    private ProgressBar counterProgressBar;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_page);

        // Initializing the views
        callButton = findViewById(R.id.callnow);
        cancelButton = findViewById(R.id.cancel);
        timeTextView = findViewById(R.id.textViewTime);
        counterProgressBar = findViewById(R.id.progressBarCircle);

        // Add event listeners to the buttons
        callButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // Starting the countdown timer
        startCountDownTimer();

        // Getting their preferred contact method and contact list
        // TODO: Get their preferred method of contact
        preferredContactMethod = getPrefContactMethod();
        // TODO: Get their contact list
        contactList = getContactList();

        // Initializing the sms manager
        smsManager = SmsManager.getDefault();


    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.callnow:
                // TODO: Contact everyone in their contact list
                alertContactList(preferredContactMethod, contactList, seizureMessage);
                break;

            case R.id.cancel:
                if(timerStatus == TimerStatus.STARTED){
                    stopCountDownTimer();
                }
                else if(timerStatus == TimerStatus.STOPPED){
                    // TODO: Contact everyone in their contact list that it was a mistake
                    alertContactList(preferredContactMethod, contactList, cancelMessage);

                    // TODO: Update the alert page back to normal
                }
                break;
        }
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private String getPrefContactMethod() {
        return "text";
    }

    private ArrayList<String> getContactList() {
        return new ArrayList<>();
    }

    private void alertContactList(String contactMethod, ArrayList<String> contactList, String message) {
        switch (contactMethod){
            case "text":
                for(String contactPerson:contactList){
                    smsManager.sendTextMessage(contactPerson, null, message, null, null);
                }
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
    private void setTimerValues() {
        int time = 0;
        // TODO: read their countdown timer time from firebase

        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeTextView.setText(hmsTimeFormatter(millisUntilFinished));

                counterProgressBar.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                // TODO: Write a new journal to Firebase

                /* TODO: Update the UI of the alert page to show that
                    it has contacted the contact list
                 */

                timerStatus = TimerStatus.STOPPED;

                timeTextView.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
            }

        }.start();
        // countDownTimer.start();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        counterProgressBar.setMax((int) timeCountInMilliSeconds / 1000);
        counterProgressBar.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
}