package com.example.seizuredetectionapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import cucumber.api.Pending;


public class ExampleService extends Service {

    private PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;
    private Thread thread;
    private Notification notification;
    private RequestQueue mRQueue;
    private StringRequest mSReq;
    PrimeThread T1;
    boolean running;

    @Override
    public void onCreate() {

        mRQueue = Volley.newRequestQueue(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");
        Log.d("input", input);

        Intent notificationIntent = new Intent(this, Navbar.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        createNotification("Reading Vitals", pendingIntent);
        T1 = new PrimeThread();

        if(input.equals("Start Service")){
            T1.start();
        } else{
            T1.interrupt();;
            stopSelf();
            stopForeground(true);
        }

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

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

    class PrimeThread extends Thread {
        boolean running = false;
        private LocalSettings localSettings;
        private MediaPlayer mp;

        @Override
        public void run() {
            running = true;
            int counter = 0;
            while(running){
                SystemClock.sleep(1000);
                Log.d("Log", String.valueOf(counter));
                counter++;
                makeOkHTTPReq();
                if(counter == 10){
                    // TODO: Test these
                    openAlertPage();
                    vibratePhone();
                    playAlarm();

                    /*
                    Intent notificationIntent = new Intent(ExampleService.this, Navbar.class);;
                    notificationIntent.putExtra("seizure", true);
                    PendingIntent pendingIntent = PendingIntent.getActivity(ExampleService.this,
                            0, notificationIntent, PendingIntent.FLAG_MUTABLE);
                    String input = "Seizure has been detected!";
                    displayNotification(input, pendingIntent);

                     */
                }
            }
        }

        private void playAlarm(){
            mp = MediaPlayer.create(ExampleService.this, Settings.System.DEFAULT_RINGTONE_URI);
            mp.start();
        }

        private void displayNotification(String input, PendingIntent pendingIntent){

            notification = new NotificationCompat.Builder(ExampleService.this, CHANNEL_ID)
                    .setContentTitle("STRapp")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        private void vibratePhone(){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }

        private void openAlertPage(){
            Intent dialogIntent = new Intent(ExampleService.this, Navbar.class);
            dialogIntent.putExtra("seizure", true);
            dialogIntent.setAction(Intent.ACTION_VIEW);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(dialogIntent);
            Log.d("startSeizureProtocol", "Navbar opened");
        }

        private void makeOkHTTPReq(){
            OkHttpClient client = new OkHttpClient();

            String url = "http://104.237.129.207:8080/detect?key=dlnPAXE2CRNuB2y9h3nPJt6n4iH9YLvONt6RSugo_yg=";

            RequestBody formBody = new FormEncodingBuilder()
                    .add("timestamp", "ass")
                    .add("reading", "ass")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();
                        Log.d("response", myResponse);
                    }
                }
            });
        }

        public void stopRunning(){
            running = false;
        }
    }
}
