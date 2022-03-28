package com.example.seizuredetectionapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertPageFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private AlertPageFragment.TimerStatus timerStatus;
    private int timeCountInMilliSeconds = 1000;

    public String userCountdownTime = "30";
    public String preferredContactMethod;
    public Set<String> contactList;
    private String seizureMessage = "Help! I'm having a seizure!";
    private String cancelMessage = "Get Punked! I didn't have a seizure";

    private SmsManager smsManager;

    private Button callButton, cancelButton;
    private TextView timeTextView, infoText;
    private ProgressBar counterProgressBar;
    private CountDownTimer countDownTimer;
    private RippleBackground rippleBackground;
    private String helpRequestSent = "Help request sent. Waiting for acknowledgement";
    private String helpRequestInitiated = "Help request has been initiated. Help request will be sent in";
    private String userAddress = "";

    public FirebaseDatabase database;
    public DatabaseReference userTable;
    private String currentUserUID;

    private int PERMISSION_ID = 44;
    private double longitude, latitude;

    private Geocoder geocoder;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocalSettings localSettings;

    public AlertPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertPageFragment newInstance(String param1, String param2) {
        AlertPageFragment fragment = new AlertPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Asking all the permissions
        // TODO: move to questionnaire page in the future
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

        // database configurations for writing journals to firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Users").child(currentUserUID);

        // Initializing the sms manager
        smsManager = SmsManager.getDefault();

        // Initializing location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }

         */

        Log.d("User Address", userAddress+" this");

        // Set necessary data and start the countdown timer
        Log.d("user info", preferredContactMethod+"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_alert_page, container, false);

        // Initializing the views
        callButton = root.findViewById(R.id.callnow);
        cancelButton = root.findViewById(R.id.cancel);
        timeTextView = root.findViewById(R.id.textViewTime);
        infoText = root.findViewById(R.id.infoText);
        counterProgressBar = root.findViewById(R.id.progressBarCircle);
        rippleBackground = root.findViewById(R.id.ripple);

        // Add event listeners to the buttons
        callButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        timerStatus = AlertPageFragment.TimerStatus.STARTED;
        startAlertPage();

        return root;
    }

    private void getLocation() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        userAddress = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                        Log.d("location", " location "+ userAddress);
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
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

                DatatableFragment nextFrag = new DatatableFragment();
                int currentFragment = ((ViewGroup)getView().getParent()).getId();

                if(timerStatus == AlertPageFragment.TimerStatus.STARTED){
                    stopCountDownTimer();
                }
                if(timerStatus == AlertPageFragment.TimerStatus.STOPPED){
                    alertContactList(preferredContactMethod, contactList, cancelMessage);
                    timerStatus = AlertPageFragment.TimerStatus.STARTED;
                    changeUI(timerStatus);
                }

                // Moving to the datatable fragment if user cancels
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(currentFragment, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
                // Updating the navbar to reflect the move to datatable
                Navbar.getBottomNavigationView().setSelectedItemId(R.id.datatableFragment);
                break;
        }
    }

    /**
     * method to stop the countdown timer and update
     * the timer status
     */
    public void stopCountDownTimer() {
        countDownTimer.cancel();
        timerStatus = AlertPageFragment.TimerStatus.STOPPED;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

        /*
        userTable.child("Settings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Questionnaire settings = snapshot.getValue(Questionnaire.class);
                AlertPageFragment.preferredContactMethod = settings.contactMethod;
                AlertPageFragment.contactList = settings.addedContacts;
                AlertPageFragment.userCountdownTime = settings.countdownTimer;

                AlertPageFragment.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Setting Data Retrieval", error.getDetails());
            }
        });

         */

        // Retrieving user info from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        preferredContactMethod = sharedPreferences.getString("preferred contact method", LocalSettings.getPreferredContactMethod());
        contactList = sharedPreferences.getStringSet("contact method", LocalSettings.getContactList());
        userCountdownTime = sharedPreferences.getString("countdown timer", LocalSettings.getCountdownTimer());
        Log.d("countdown time", ""+userCountdownTime);

        start();

    }

    /**
     * helper method to allow startAlertPage() to initialize the
     * countdown timer
     */
    private void start() {
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
    private void alertContactList(String contactMethod, Set<String> contactList, String message) {
        getLocation();

        switch (contactMethod){
            case "text message":

                /*for(String contactPerson:contactList){
                    Set<String> names = contactPerson.keySet();
                    String name = names.iterator().next();
                    smsManager.sendTextMessage(contactPerson.get(name), null, message+"\n"+userAddress, null, null);
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
    public void setTimerValues() {
        int countdownTime = Integer.parseInt(userCountdownTime);

        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = countdownTime * 1000;
    }

    /**
     * method to start count down timer
     */
    public void startCountDownTimer() {

        setProgressBarValues();
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 50) {
            @Override
            public void onTick(long millisUntilFinished) {

                // Updating the countdown text
                timeTextView.setText(millisUntilFinished/1000 + " Sec");

                // Updating the progress bar to reflect the change smoothly
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
                timerStatus = AlertPageFragment.TimerStatus.STOPPED;
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
    private void saveJournal(){
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").
                format(Calendar.getInstance().getTime());
        List<String> moodType = new ArrayList<String>();
        List<String> seizureType = new ArrayList<String>();
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
                        // TODO: figure out the bug with the getActivity when you leave the alert page via the navbar
                        // Toast.makeText(getActivity(), "Journal saved", Toast.LENGTH_LONG).show();
                        Log.d("Update", "journal saved");
                    }
                    else{
                        Toast.makeText(getActivity(), "Journal error!", Toast.LENGTH_LONG).show();
                        Log.d("Journal Error: ", task.getException().getLocalizedMessage());
                    }
                });
    }

    /**
     * method to update the UI based on the status of
     * the countdown timer
     */
    private void changeUI(AlertPageFragment.TimerStatus timerStatus) {
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
    private void setProgressBarValues() {

        counterProgressBar.setMax(timeCountInMilliSeconds / 50);
        counterProgressBar.setProgress(timeCountInMilliSeconds / 1000);
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