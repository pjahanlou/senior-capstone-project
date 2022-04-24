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
import cucumber.api.Pending;


public class ExampleService extends Service {

    private Notification notification;
    PrimeThread T1;
    public static MediaPlayer mp;
    public String seizurePrediction;
    boolean running = true;
    public static NotificationCompat.Builder seizureNotification;
    public static int seizureNotificationID = 101;
    public static NotificationManagerCompat notificationManager;
    private static boolean seizureDetected = false;

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
            userCountdownTime = Integer.parseInt(sharedPreferences.getString("countdown timer", ""));

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
