/*
package com.example.seizuredetectionapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.ServiceState;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static com.example.seizuredetectionapp.App.CHANNEL_ID;
import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seizuredetectionapp.profile.STRappBleManager;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.picocontainer.annotations.Inject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Random;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import cucumber.api.Pending;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.seizuredetectionapp.adapter.DiscoveredBluetoothDevice;
import com.example.seizuredetectionapp.databinding.ActivityBleBinding;
import com.example.seizuredetectionapp.viewmodels.STRappBleViewModel;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;
import no.nordicsemi.android.ble.observer.ConnectionObserver;


public class ExampleService extends Service {

    private Notification notification;
    private PrimeThread T1 = new PrimeThread();
    public static MediaPlayer mp;
    public String seizurePrediction;
    boolean running = true;
    public static NotificationCompat.Builder seizureNotification;
    public static int seizureNotificationID = 101;
    public static NotificationManagerCompat notificationManager;
    private static boolean seizureDetected = false;
    private HashMap<String, String> personalInfo = new HashMap<>();
    private DiscoveredBluetoothDevice device;
    private STRappBleViewModel viewModel;
    private STRappBleManager BleManager;
    private SharedPreferences sharedPreferences;
    private LocalSettings localSettings;
//    @Inject
//    STRappBleViewModel viewModel;

    @Override
    public void onCreate() {

        super.onCreate();

        //viewModel = new STRappBleViewModel(getApplication());
        //BleManager = new STRappBleManager(getApplicationContext());
        Log.d("tag1", "made it here");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        String input = intent.getStringExtra("inputExtra");
        Log.d("Service Status", input);

        // Creating the seizure detection notification
        Intent notificationIntent = new Intent(this, Navbar.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, notificationIntent, 0);
        createNotification("Reading Vitals", pendingIntent);
        startForeground(1, notification);

        getPersonalInfo();
        Log.d("personal info", personalInfo.toString());
        sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE);

        // Starting the service thread
        Log.d("input", input);
        if (input.equals("Start Service")) {
            T1.start();
            device = loadBLEDevice();
            Log.d("device", String.valueOf(device));
//            viewModel.connect(device);
//            viewModel.getConnectionState();
        } else {
            stopThread();
            stopSelf();
            stopForeground(true);
        }

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPersonalInfo(){
        LocalSettings localSettings = new LocalSettings();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        int currentYear = LocalDateTime.now().getYear();
        Log.d("now", currentYear+"");

        SharedPreferences sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE);
        String age = sharedPreferences.getString("age", localSettings.getAge());
        String gender = sharedPreferences.getString("sex", localSettings.getSex());
        String height = sharedPreferences.getString("height", localSettings.getHeight());
        String weightstr = sharedPreferences.getString("weight", localSettings.getWeight());
        String weight = "200";
        if (weightstr != null) {
            weight = String.valueOf(Math.round(Float.valueOf(weightstr)));
        }


        String ageYear = age != null ? age.split("/")[2] : "2000";
        String ageDifference = String.valueOf(currentYear - Integer.parseInt(ageYear));

        String genderConverted = null;
        if(gender != null && gender.equals("Male")){
            genderConverted = "1";
        } else{
            genderConverted = "0";
        }

        String heightCm = height != null ? convertToCm(height) : "180";

        personalInfo.put("Age", ageDifference);
        personalInfo.put("Gender", genderConverted);
        personalInfo.put("Height", heightCm);
        personalInfo.put("Weight", weight);
    }

    private String convertToCm(String height){
        String heightCm = null;
        int feet = Integer.parseInt(height.split("'")[0]);
        int inches = Integer.parseInt(height.split("'")[1]);
        heightCm = String.valueOf(Math.round((feet * 30.48) + (inches * 2.54)));
        return heightCm;
    }

    */
/**
     * Method for building the seizure detection notification
     * *//*

    public void createNotification(String input, PendingIntent pendingIntent) {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("STRapp")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //super.onBind(intent);
        return null;
    }

    */
/**
     * Class for everything related to the thread that runs our service
     * *//*

    class PrimeThread extends Thread {
        private LocalSettings localSettings;
        private SharedPreferences sharedPreferences;
        private int userCountdownTime;
        private int timer = 0;
        private String input = "Seizure has been detected!";

        @Override
        public void run() {
            int counter = 0;
            Log.d("running", String.valueOf(running));
            while(running){
                SystemClock.sleep(1000);
                Log.d("Log", String.valueOf(counter));
                counter++;
                // TODO: Ble calls
                if (HTTPHelpers.Debug()) {
                    // Post fake HR, acceleration, gyro, and EDA
                    try {
                        postFakeData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                makeOkHTTPReq();
                // TODO: Change to response from microservice in the near future
                if(counter == 10){
                    seizureDetected = true;

                    // TODO: Fix default sound bug
                    openAlertPage();
                    vibratePhone();
                    playAlarm();

                    // Creating the seizure detected notification
                    displayNotification();
                }

                // Updating the notification progress bar
                if(seizureDetected){
                    seizureNotification.setProgress(userCountdownTime, timer, false);
                    seizureNotification.setContentText(input+"\n"+(userCountdownTime-timer)+" Sec Until Contacts Notified");
                    notificationManager.notify(seizureNotificationID, seizureNotification.build());
                    timer++;
                    // Change text when countdown timer runs out of time
                    if(timer == userCountdownTime){
                        seizureNotification.setContentText("Emergency contacts have been notified.");
                        seizureNotification.setProgress(0, 0, false);
                        notificationManager.notify(seizureNotificationID, seizureNotification.build());
                        seizureDetected = false;
                        // TODO: do we text their emergency contacts when the timer on notification runs out?
                        //  since we we already open up alert page in the background
                        //  If we do open up alert page, then close it on stop alarm so we don't contact people
                    }
                }
            }
        }

        */
/**
         * Method for playing alarm for the user
         * *//*

        private void playAlarm(){
            mp = MediaPlayer.create(ExampleService.this, Settings.System.DEFAULT_RINGTONE_URI);
            mp.start();
        }

        */
/**
         * Method for creating seizure detected notification
         * *//*

        private void displayNotification(){

            // These are for the notification itself
            Intent notificationIntent = new Intent(ExampleService.this, Navbar.class);;
            notificationIntent.putExtra("seizure", true);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ExampleService.this,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);

            // Pulling user countdown timer from shared preferences
            sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
            if(!sharedPreferences.getString("countdown timer", "").equals("")){
                userCountdownTime = Integer.parseInt(sharedPreferences.getString("countdown timer", ""));
            } else{
                userCountdownTime = 30;
            }

            // These are for the stop alarm button
            Intent snoozeIntent = new Intent(ExampleService.this, StopAlarmListener.class);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(ExampleService.this, 0,
                    snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Building the seizure detected notification
            seizureNotification = new NotificationCompat.Builder(ExampleService.this, CHANNEL_ID)
                    .setContentTitle("STRapp")
                    .setContentText(input+"\n"+userCountdownTime+" Sec")
                    .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_delete, "Stop Alarm",
                            snoozePendingIntent)
                    .setFullScreenIntent(snoozePendingIntent, true)
                    .setProgress(userCountdownTime, 0, false);

            notificationManager = NotificationManagerCompat.from(ExampleService.this);
            notificationManager.notify(seizureNotificationID, seizureNotification.build());
        }

        */
/**
         * Method for vibrating the user phone
         * *//*

        private void vibratePhone(){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(5000);
            }
        }

        */
/**
         * Method for opening alert page for when a seizure has been detected
         * *//*

        private void openAlertPage(){
            Intent dialogIntent = new Intent(ExampleService.this, Navbar.class);
            dialogIntent.putExtra("seizure", true);
            dialogIntent.setAction(Intent.ACTION_VIEW);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(dialogIntent);
            Log.d("startSeizureProtocol", "Alertpage opened");
        }

        */
/**
         * Method for making HTTP request to microservice
         * *//*

        private void makeOkHTTPReq(){
            OkHttpClient client = new OkHttpClient();

            // TODO: Put user info here
            String url = "http://104.237.129.207:8080/iris/api/v1.0/getpred?" +
                    "key=dlnPAXE2CRNuB2y9h3nPJt6n4iH9YLvONt6RSugo_yg=?" +
                    "aX=0.5743198755375472&" +
                    "aY=0.29907562940537&" +
                    "aZ=-0.051028125757191845&" +
                    "Temp=31.00307301767742&" +
                    "EDA=3.695537166802325&" +
                    "Hr=114.76355888505616&" +
                    "Age=28"+personalInfo.get("Age")+
                    "&" +
                    "Gender=1"+personalInfo.get("Gender")+
                    "&" +
                    "Height=172"+personalInfo.get("Height")+
                    "&" +
                    "Weight="+personalInfo.get("Weight");

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseString = response.body().string();
                        seizurePrediction = parseResponse(responseString);
                        if(seizurePrediction != null){
                            Log.d("response", "prediction is " + seizurePrediction);
                        } else{
                            Log.d("response", "prediction is null");
                        }
                    }
                }
            });
        }
        private void postFakeData() throws JSONException {
            // Start with EDA
            String s = "eda";
            JSONObject obj = new JSONObject();
            Random r = new Random();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            try {
                Process process = Runtime.getRuntime().exec("logcat");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                StringBuilder log=new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("12852-12852/com.example.seizuredetectionapp A/STRappBleManager:")){
                        log.append(line);
                    }
                }
                log.toString();
                Log.d("FUCK", log.toString());
            }
            catch (IOException e) {}

//            LiveData<String> AccX = viewModel.getAccxData();
//            LiveData<String> AccY = viewModel.getAccyData();
//            LiveData<String> AccZ = viewModel.getAcczData();
//            LiveData<Integer> HRM = viewModel.getHrmData();
//            AccX.observe(ExampleService.this, new Observer<String>() {
//                @Override
//                public void onChanged(String s) {
//                    Log.d("AccX", s);
//                }
//            });
//            Log.d("AccX", AccX.getValue());
//            Log.d("HRM", String.valueOf(HRM));
//            Log.d("AccX", String.valueOf(AccX));
//            Log.d("AccY", String.valueOf(AccY));
//            Log.d("AccZ", String.valueOf(AccZ));
//            Log.d("HRM", String.valueOf(HRM));

            if (CachedData.getUserKey() == "") {
                return;
            }

            String key = "?key=" + CachedData.getUserKey();

            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            obj.put("reading", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
//            CachedData.addEDA(obj.getInt("timestamp"), (float)obj.getDouble("reading"));

            JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
//                        Log.d("BackgroundProcess /eda", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /eda", error.toString());
                    }
            );

            queue.add(req);

            // HR
            s = "hr";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            ArrayList<CachedData.CacheNode> node = CachedData.HRReadings;
            int last = node.size() != 0 ? Math.round(node.get(0).value) : 100;
            int plus = 0;
            if (r.nextInt(4) != 0) {
                plus = r.nextBoolean() ? r.nextInt(3) : -r.nextInt(2);
                last += plus;
            }
            if (last < 90) last = 90;
            else if (last > 110) last = 110;
            obj.put("reading", last);
            CachedData.addHR(obj.getInt("timestamp"), last);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /hr", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /hr", error.toString());
                    }
            );
            queue.add(req);

            s = "acc";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            JSONObject subobj = new JSONObject();
            subobj.put("x", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("y", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("z", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            obj.put("reading", subobj);
            float length = (float)Math.sqrt(subobj.getDouble("x") * subobj.getDouble("x") + subobj.getDouble("y") * subobj.getDouble("y") + subobj.getDouble("z") * subobj.getDouble("z"));
            CachedData.addMM(obj.getInt("timestamp"), Math.round(length * 10.f) / 10.f);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /acc", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /acc", error.toString());
                    }
            );
            queue.add(req);

            s = "gyro";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            subobj.put("x", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("y", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("z", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            obj.put("reading", subobj);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /gyro", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /gyro", error.toString());
                    }
            );
            queue.add(req);
        }
    }

    public void stopThread(){
        running = false;
        Log.d("running", String.valueOf(running));
    }
    public DiscoveredBluetoothDevice loadBLEDevice(){
        Gson gson = new Gson();
        String deviceJson = sharedPreferences.getString("device", "");
        DiscoveredBluetoothDevice device = gson.fromJson(deviceJson, DiscoveredBluetoothDevice.class);
        return device;
    }

    public String parseResponse(String response){
        JSONObject jsonResponse = null;
        String prediction = null;
        try {
            jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("result");
            for(int i=1;i<results.length();i++){
                prediction = results.getJSONObject(1).getString("probability");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prediction;
    }

    */
/**
     * Class for handling the seizure detected "stop alarm" button
     * *//*

    public static class StopAlarmListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            mp.stop();
            seizureNotification.setProgress(0, 0, false);
            seizureNotification.setContentText("Countdown Cancelled.");
            notificationManager.notify(seizureNotificationID, seizureNotification.build());
            seizureDetected = false;
        }
    }
}
*/
package com.example.seizuredetectionapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.ServiceState;
import android.util.Log;
import android.widget.Toast;

import static com.example.seizuredetectionapp.App.CHANNEL_ID;
import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import cucumber.api.Pending;


public class ExampleService extends Service {

    private Notification notification;
    private PrimeThread T1 = new PrimeThread();
    public static MediaPlayer mp;
    public String seizurePrediction;
    boolean running = true;
    public static NotificationCompat.Builder seizureNotification;
    public static int seizureNotificationID = 101;
    public static NotificationManagerCompat notificationManager;
    private static boolean seizureDetected = false;
    private HashMap<String, String> personalInfo = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");
        Log.d("Service Status", input);

        // Creating the seizure detection notification
        Intent notificationIntent = new Intent(this, Navbar.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, notificationIntent, 0);
        createNotification("Reading Vitals", pendingIntent);
        startForeground(1, notification);

        getPersonalInfo();
        Log.d("personal info", personalInfo.toString());

        // Starting the service thread
        Log.d("input", input);
        if(input.equals("Start Service")){
            T1.start();
        } else{
            stopThread();
            stopSelf();
            stopForeground(true);
        }

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPersonalInfo(){
        LocalSettings localSettings = new LocalSettings();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        int currentYear = LocalDateTime.now().getYear();
        Log.d("now", currentYear+"");

        SharedPreferences sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE);
        String age = sharedPreferences.getString("age", localSettings.getAge());
        String gender = sharedPreferences.getString("sex", localSettings.getSex());
        String height = sharedPreferences.getString("height", localSettings.getHeight());
        String weight = String.valueOf(Math.round(Float.valueOf(sharedPreferences.getString("weight", localSettings.getWeight()))));

        String ageYear = age.split("/")[2];
        String ageDifference = String.valueOf(currentYear - Integer.parseInt(ageYear));

        String genderConverted = null;
        if(gender.equals("Male")){
            genderConverted = "1";
        } else{
            genderConverted = "0";
        }

        String heightCm = convertToCm(height);

        personalInfo.put("Age", ageDifference);
        personalInfo.put("Gender", genderConverted);
        personalInfo.put("Height", heightCm);
        personalInfo.put("Weight", weight);
    }

    private String convertToCm(String height){
        String heightCm = null;
        int feet = Integer.parseInt(height.split("'")[0]);
        int inches = Integer.parseInt(height.split("'")[1]);
        heightCm = String.valueOf(Math.round((feet * 30.48) + (inches * 2.54)));
        return heightCm;
    }

    /**
     * Method for building the seizure detection notification
     * */
    public void createNotification(String input, PendingIntent pendingIntent) {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("STRapp")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Class for everything related to the thread that runs our service
     * */
    class PrimeThread extends Thread {
        private LocalSettings localSettings;
        private SharedPreferences sharedPreferences;
        private int userCountdownTime;
        private int timer = 0;
        private String input = "Seizure has been detected!";

        @Override
        public void run() {
            int counter = 0;
            Log.d("running", String.valueOf(running));
            while(running){
                SystemClock.sleep(1000);
                Log.d("Log", String.valueOf(counter));
                counter++;
                if (HTTPHelpers.Debug()) {
                    // Post fake HR, acceleration, gyro, and EDA
                    try {
                        postFakeData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                makeOkHTTPReq();
                // TODO: Change to response from microservice in the near future
                if(counter == 10){
                    seizureDetected = true;

                    // TODO: Fix default sound bug
                    openAlertPage();
                    vibratePhone();
                    playAlarm();

                    // Creating the seizure detected notification
                    displayNotification();
                }

                // Updating the notification progress bar
                if(seizureDetected){
                    seizureNotification.setProgress(userCountdownTime, timer, false);
                    seizureNotification.setContentText(input+"\n"+(userCountdownTime-timer)+" Sec Until Contacts Notified");
                    notificationManager.notify(seizureNotificationID, seizureNotification.build());
                    timer++;
                    // Change text when countdown timer runs out of time
                    if(timer == userCountdownTime){
                        seizureNotification.setContentText("Emergency contacts have been notified.");
                        seizureNotification.setProgress(0, 0, false);
                        notificationManager.notify(seizureNotificationID, seizureNotification.build());
                        seizureDetected = false;
                        // TODO: do we text their emergency contacts when the timer on notification runs out?
                        //  since we we already open up alert page in the background
                        //  If we do open up alert page, then close it on stop alarm so we don't contact people
                    }
                }
            }
        }

        /**
         * Method for playing alarm for the user
         * */
        private void playAlarm(){
            mp = MediaPlayer.create(ExampleService.this, Settings.System.DEFAULT_RINGTONE_URI);
            mp.start();
        }

        /**
         * Method for creating seizure detected notification
         * */
        private void displayNotification(){

            // These are for the notification itself
            Intent notificationIntent = new Intent(ExampleService.this, Navbar.class);;
            notificationIntent.putExtra("seizure", true);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ExampleService.this,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);

            // Pulling user countdown timer from shared preferences
            sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
            if(!sharedPreferences.getString("countdown timer", "").equals("")){
                userCountdownTime = Integer.parseInt(sharedPreferences.getString("countdown timer", ""));
            } else{
                userCountdownTime = 30;
            }

            // These are for the stop alarm button
            Intent snoozeIntent = new Intent(ExampleService.this, StopAlarmListener.class);
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(ExampleService.this, 0,
                    snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Building the seizure detected notification
            seizureNotification = new NotificationCompat.Builder(ExampleService.this, CHANNEL_ID)
                    .setContentTitle("STRapp")
                    .setContentText(input+"\n"+userCountdownTime+" Sec")
                    .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_delete, "Stop Alarm",
                            snoozePendingIntent)
                    .setFullScreenIntent(snoozePendingIntent, true)
                    .setProgress(userCountdownTime, 0, false);

            notificationManager = NotificationManagerCompat.from(ExampleService.this);
            notificationManager.notify(seizureNotificationID, seizureNotification.build());
        }

        /**
         * Method for vibrating the user phone
         * */
        private void vibratePhone(){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(5000);
            }
        }

        /**
         * Method for opening alert page for when a seizure has been detected
         * */
        private void openAlertPage(){
            Intent dialogIntent = new Intent(ExampleService.this, Navbar.class);
            dialogIntent.putExtra("seizure", true);
            dialogIntent.setAction(Intent.ACTION_VIEW);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(dialogIntent);
            Log.d("startSeizureProtocol", "Alertpage opened");
        }

        /**
         * Method for making HTTP request to microservice
         * */
        private void makeOkHTTPReq(){
            OkHttpClient client = new OkHttpClient();

            // TODO: Put user info here
            String url = "http://104.237.129.207:8080/iris/api/v1.0/getpred?" +
                    "key=dlnPAXE2CRNuB2y9h3nPJt6n4iH9YLvONt6RSugo_yg=?" +
                    "aX=0.5743198755375472&" +
                    "aY=0.29907562940537&" +
                    "aZ=-0.051028125757191845&" +
                    "Temp=31.00307301767742&" +
                    "EDA=3.695537166802325&" +
                    "Hr=114.76355888505616&" +
                    "Age=28"+personalInfo.get("Age")+
                    "&" +
                    "Gender=1"+personalInfo.get("Gender")+
                    "&" +
                    "Height=172"+personalInfo.get("Height")+
                    "&" +
                    "Weight="+personalInfo.get("Weight");

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseString = response.body().string();
                        seizurePrediction = parseResponse(responseString);
                        if(seizurePrediction != null){
                            Log.d("response", "prediction is " + seizurePrediction);
                        } else{
                            Log.d("response", "prediction is null");
                        }
                    }
                }
            });
        }
        private void postFakeData() throws JSONException {
            // Start with EDA
            String s = "eda";
            JSONObject obj = new JSONObject();
            Random r = new Random();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            if (CachedData.getUserKey() == "") {
                return;
            }

            String key = "?key=" + CachedData.getUserKey();

            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            obj.put("reading", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            //CachedData.addEDA(obj.getInt("timestamp"), (float)obj.getDouble("reading"));

            JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
//                        Log.d("BackgroundProcess /eda", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /eda", error.toString());
                    }
            );

            queue.add(req);

            // HR
            s = "hr";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            ArrayList<CachedData.CacheNode> node = CachedData.HRReadings;
            int last = node.size() != 0 ? Math.round(node.get(0).value) : 105;
            int plus = 0;
            if (r.nextInt(5) != 0) {
                plus = r.nextBoolean() ? r.nextInt(3) : -r.nextInt(2);
                last += plus;
            }
            if (last < 95) last = 95;
            else if (last > 115) last = 115;
            obj.put("reading", last);
            CachedData.addHR(obj.getInt("timestamp"), last);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /hr", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /hr", error.toString());
                    }
            );
            queue.add(req);

            s = "acc";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            JSONObject subobj = new JSONObject();
            subobj.put("x", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("y", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("z", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            obj.put("reading", subobj);
            float length = (float)Math.sqrt(subobj.getDouble("x") * subobj.getDouble("x") + subobj.getDouble("y") * subobj.getDouble("y") + subobj.getDouble("z") * subobj.getDouble("z"));
            //CachedData.addMM(obj.getInt("timestamp"), Math.round(length * 10.f) / 10.f);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /acc", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /acc", error.toString());
                    }
            );
            queue.add(req);

            s = "gyro";
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            subobj.put("x", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("y", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            subobj.put("z", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            obj.put("reading", subobj);

            req = new JsonObjectRequest(com.android.volley.Request.Method.POST, HTTPHelpers.MYURL + s + key, obj,
                    response -> {
                        // Okay
//                        Log.d("BackgroundProcess /gyro", response.toString());
                    },
                    error -> {
                        Log.d("BackgroundProcess /gyro", error.toString());
                    }
            );
            queue.add(req);
        }
    }

    public void stopThread(){
        running = false;
        Log.d("running", String.valueOf(running));
    }

    public String parseResponse(String response){
        JSONObject jsonResponse = null;
        String prediction = null;
        try {
            jsonResponse = new JSONObject(response);
            JSONArray results = jsonResponse.getJSONArray("result");
            for(int i=1;i<results.length();i++){
                prediction = results.getJSONObject(1).getString("probability");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prediction;
    }

    /**
     * Class for handling the seizure detected "stop alarm" button
     * */
    public static class StopAlarmListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            mp.stop();
            seizureNotification.setProgress(0, 0, false);
            seizureNotification.setContentText("Countdown Cancelled.");
            notificationManager.notify(seizureNotificationID, seizureNotification.build());
            seizureDetected = false;
        }
    }
}