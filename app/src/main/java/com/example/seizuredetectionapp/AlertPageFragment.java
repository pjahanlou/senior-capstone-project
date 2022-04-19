package com.example.seizuredetectionapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public enum TimerStatus {
        STARTED,
        STOPPED
    }

    private AlertPageFragment.TimerStatus timerStatus;
    private int timeCountInMilliSeconds = 1000;

    public String userCountdownTime = "30";
    public String preferredContactMethod;
    public Map<String, String> contactList = new HashMap<>();
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
    private Double userLatitude, userLongitude;
    private ImageView hintImage;
    private TextView textBox, titleBox;
    private LocalSettings localSettings;

    private boolean locationPermission, textPermission;
    private Set<String> usualLocations = new HashSet<>();

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

        // Check to see if they have given permission for location
        locationPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
        if (!locationPermission) {
            String descriptionLocation = "Unfortunately, help request is unavailable. Please give STRapp location access, so it can better notify your contacts.";
            String titleLocation = "Location Permission";
            String buttonText = "Sure!";
            showMessage(getContext(), descriptionLocation, titleLocation, buttonText);
        }

        // Check to see if they have given permission for text
        textPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED;
        if (!textPermission) {
            String descriptionText = "Unfortunately, help request is unavailable. Please give STRapp SMS access, so it can better notify your contacts.";
            String titleText = "SMS Permission";
            String buttonText = "Sure!!";
            showMessage(getContext(), descriptionText, titleText, buttonText);
        }

        // Check to see if they have given usual locations
        usualLocations = pullLocationsFromLocalSettings();
        if(usualLocations.isEmpty()){
            String descriptionUsualLocation = "Unfortunately, help request is unavailable. Please add your usual locations in App Settings, so STRapp can better notify your contacts.";
            String titleUsualLocation = "Usual Locations";
            String buttonText = "Add Locations";
            showMessage(getContext(), descriptionUsualLocation, titleUsualLocation, buttonText);
        }

        // Check to see if they have given contacts
        contactList = loadContactMap();
        if(contactList.isEmpty()){
            String descriptionContact = "Unfortunately, help request is unavailable. Please add your emergency contacts in App Settings, so STRapp can better notify your contacts.";
            String titleContact = "Emergency Contacts";
            String buttonText = "Add Contacts";
            showMessage(getContext(), descriptionContact, titleContact, buttonText);
        }

        // database configurations for writing journals to firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        userTable = database.getReference("Users").child(currentUserUID);

        // Initializing the sms manager
        smsManager = SmsManager.getDefault();

        // Initializing location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

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
        hintImage = root.findViewById(R.id.hintFragmentAlertPage);

        // Add event listeners to the buttons
        callButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        hintImage.setOnClickListener(this);

        // If all permissions are there, start the page
        if(textPermission && locationPermission && !usualLocations.isEmpty() && !contactList.isEmpty()){
            getLocation();
            timerStatus = AlertPageFragment.TimerStatus.STARTED;
            startAlertPage();
        }

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
                        userLatitude = location.getLatitude();
                        userLongitude = location.getLongitude();
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
                alertContactList(seizureMessage);
                stopCountDownTimer();
                changeUI(timerStatus);
                saveJournal();
                break;

            case R.id.cancel:

                if(timerStatus == AlertPageFragment.TimerStatus.STARTED){
                    stopCountDownTimer();
                }
                if(timerStatus == AlertPageFragment.TimerStatus.STOPPED){
                    alertContactList(cancelMessage);
                    timerStatus = AlertPageFragment.TimerStatus.STARTED;
                    changeUI(timerStatus);
                }

                startDatatable();
                break;
            case R.id.hintFragmentAlertPage:
                String description = "When a seizure has been detected your emergency contacts will be notified after a countdown. If the countdown starts and" +
                                        " you do not believe you are having a seizure, you can press the cancel button at the bottom.";
                String title = "Help Request";
                showHint(v.getContext(), description, title);
                break;
        }
    }

    private void startDatatable(){
        startActivity(new Intent(getContext(), Navbar.class));
    }

    private void showHint(Context context, String description, String title) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_settings_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        textBox = dialog.getWindow().findViewById(R.id.textView2);
        titleBox = dialog.getWindow().findViewById(R.id.textView);
        textBox.setText(description);
        titleBox.setText(title);

        Button gotIt = dialog.findViewById(R.id.btn_gotit);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showMessage(Context context, String description, String title, String buttonText) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_newuser_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        textBox = dialog.getWindow().findViewById(R.id.textView2);
        titleBox = dialog.getWindow().findViewById(R.id.textView);
        textBox.setText(description);
        titleBox.setText(title);

        Button okay = dialog.findViewById(R.id.btn_okay);
        okay.setText(buttonText);

        Button cancel = dialog.findViewById(R.id.btn_cancel);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch(buttonText){
                    case "Sure!!":
                        intent = new Intent(getContext(), TextPermission.class);
                        break;
                    case "Sure!":
                        intent = new Intent(getContext(), LocationPermission.class);
                        break;
                    case "Add Contacts":
                        intent = new Intent(getContext(), UpdateContacts.class);
                        break;
                    case "Add Locations":
                        intent = new Intent(getContext(), UsualLocations.class);
                        break;
                }
                intent.putExtra("page", "alert page");
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startDatatable();
            }
        });

        dialog.show();
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

        // Retrieving user info from shared preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        preferredContactMethod = sharedPreferences.getString("preferred contact method", LocalSettings.getPreferredContactMethod());
        Log.d("preferred contact", ""+preferredContactMethod);
        userCountdownTime = sharedPreferences.getString("countdown timer", LocalSettings.getCountdownTimer());
        Log.d("countdown time", ""+userCountdownTime);

        // Pulling the contact list
        Log.d("preferred contact", ""+contactList.toString());

        start();

    }

    /**
     * Method for pulling the contact hashmap
     * */
    private Map<String, String> loadContactMap() {
        Map<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getActivity().getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("contact map", (new JSONObject()).toString());
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonObject.getString(key);
                        outputMap.put(key, value);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return outputMap;
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
    private void alertContactList(String message) {
        // Setting the user location to userAddress variable
        //getLocation();

        // Pulling the usual locations from local settings
        String closestLocation = "";

        // Checking if the user is near any of their usual locations
        if(usualLocations != null){
            closestLocation = findClosestLocation();
        }

        switch (preferredContactMethod){
            case "Text Message":

                Iterator hmIterator = contactList.entrySet().iterator();
                while(hmIterator.hasNext()){
                    Map.Entry contact = (Map.Entry)hmIterator.next();
                    String name = (String) contact.getValue();
                    String number = (String) contact.getKey();
                    Log.d("number", ""+number);
                    Log.d("location", ""+userAddress);
                    Log.d("closestLocation", ""+closestLocation);
                    Log.d("conditional", String.valueOf(!closestLocation.equals("")));
                    if(!closestLocation.equals("") && !message.equals(cancelMessage)){
                        Log.d("conditional2", String.valueOf(!closestLocation.equals("")));
                        smsManager.sendTextMessage(number, null,
                                    "Hi "+name+",\n"+
                                        message+"\nCurrent location:"+userAddress+
                                        "\nUsual Location: " + closestLocation
                                , null, null);
                    } else if(message.equals(cancelMessage)){
                        smsManager.sendTextMessage(number, null, "Hello "+name+",\n"+
                                        message
                                , null, null);
                    }
                    else if(closestLocation.equals("") && !message.equals(cancelMessage)){
                        smsManager.sendTextMessage(number, null, "Hello "+name+",\n"+
                                message+"\nwe have detected that they're here:"+userAddress
                                , null, null);
                    }
                }

                break;

            case "email":
                break;
        }
    }

    private String findClosestLocation(){
        String closestLocation = "";

        for(String location:usualLocations){
            // Getting the coordinates of a usual location
            LatLng latLng = getLocationFromAddress(location);
            Log.d("latlng", latLng.toString());

            // Setting coordinate points
            Location userLocation = new Location("user location");
            userLocation.setLatitude(userLatitude);
            userLocation.setLongitude(userLongitude);

            Location usualLocation = new Location("usual location");
            usualLocation.setLatitude(latLng.latitude);
            usualLocation.setLongitude(latLng.longitude);

            // Calculating the distance
            float distance = userLocation.distanceTo(usualLocation);
            Log.d("distance", String.valueOf(distance));

            // If the user is within half a mile of a usual location
            if(distance <= 800){
                closestLocation = location;
            }
        }

        return closestLocation;
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
     * Method for converting address to Coordinates
     * */
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
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
                alertContactList(seizureMessage);

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
     * Method for pulling usual locations from local settings
     * */
    public Set<String> pullLocationsFromLocalSettings(){
        Set<String> locations = new HashSet<>();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        locations = sharedPreferences.getStringSet("locations", LocalSettings.getLocations());

        return locations;
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