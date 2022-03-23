package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AlertPage extends AppCompatActivity implements View.OnClickListener {

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private static TimerStatus timerStatus;
    private static long timeCountInMilliSeconds;

    public static String userCountdownTime = "30";
    public static String preferredContactMethod;
    public static Set<String> contactList;
    private static String seizureMessage = "Help! I'm having a seizure!";
    private static String cancelMessage = "Get Punked! I didn't have a seizure";

    private static SmsManager smsManager;

    private static Button callButton, cancelButton;
    private static TextView timeTextView, infoText;
    private static ProgressBar counterProgressBar;
    private static CountDownTimer countDownTimer;
    private static RippleBackground rippleBackground;
    private static Context context;
    private static String helpRequestSent = "Help request sent. Waiting for acknowledgement";
    private static String helpRequestInitiated = "Help request has been initiated. Help request will be sent in";
    private static String userAddress = "";

    public static FirebaseDatabase database;
    public static DatabaseReference userTable;
    private static String currentUserUID;

    private static int PERMISSION_ID = 44;
    private static double longitude, latitude;

    private static Geocoder geocoder;
    private LocationRequest locationRequest;
    private static FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_page);

        // Asking all the permissions
        // TODO: move to questionnaire page in the future
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

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

        // Initializing location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }

         */

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("location", " location "+ location);
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        Log.d("User Address", userAddress+" this");

        // Set necessary data and start the countdown timer
        timerStatus = TimerStatus.STARTED;
        startAlertPage();

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        userAddress += addresses.get(0).getCountryName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("Current Address", strReturnedAddress.toString());
            } else {
                Log.w("Current Address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current Address", "Cannot get Address!");
        }
        return strAdd;
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
    private static void alertContactList(String contactMethod, Set<String> contactList, String message) {
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
                alertContactList(preferredContactMethod, contactList, seizureMessage);

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
        List<String> seizureTrigger = new ArrayList<String>();
        String seizureDescription = "";
        String postSeizureDescription = "";
        String severity = "";

        Journal newJournal = new Journal(timeStamp, moodType, seizureType, durationOfSeizure,
                seizureTrigger, seizureDescription, postSeizureDescription, severity);

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