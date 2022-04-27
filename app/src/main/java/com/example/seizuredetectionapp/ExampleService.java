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
import java.util.Random;

import cucumber.api.Pending;


public class ExampleService extends Service {

    private Notification notification;
    PrimeThread T1;
    public static MediaPlayer mp;
    public String seizurePrediction;
    boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();
    }

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

        // Starting the service thread
        // TODO: Figure out a way to stop the thread
        T1 = new PrimeThread();
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
        private NotificationCompat.Builder seizureNotification;
        private int seizureNotificationID = 101;
        private NotificationManagerCompat notificationManager;
        private SharedPreferences sharedPreferences;
        private int userCountdownTime;
        private boolean seizureDetected = false;
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

                    // TODO: Test these
                    //openAlertPage();
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

            String url = "http://104.237.129.207:8080/iris/api/v1.0/getpred?key=dlnPAXE2CRNuB2y9h3nPJt6n4iH9YLvONt6RSugo_yg=?aX=1&aY=1&aZ=5&Temp=100&EDA=1&Hr=80";

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
            CachedData.addEDA(obj.getInt("timestamp"), (float)obj.getDouble("reading"));

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
            obj = new JSONObject();
            obj.put("timestamp", System.currentTimeMillis() / 1000L);
            obj.put("reading", Math.round(r.nextFloat() * 100.0 * 10.f) / 10.f);
            CachedData.addHR(obj.getInt("timestamp"), (float)obj.getDouble("reading"));

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
        }
    }
}
