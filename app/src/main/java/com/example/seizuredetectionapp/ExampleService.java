package com.example.seizuredetectionapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.ServiceState;
import android.util.Log;
import android.widget.Toast;

import static com.example.seizuredetectionapp.App.CHANNEL_ID;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;
import com.google.firebase.database.core.utilities.Utilities;
import com.squareup.okhttp.Call;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cucumber.api.Pending;


public class ExampleService extends Service {

    private PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;
    private Thread thread;
    private Notification notification;
    private RequestQueue mRQueue;
    private StringRequest mSReq;
    PrimeThread T1;

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

        createNotification(input, pendingIntent);
        T1=new PrimeThread();

        if(input.equals("Start Service")){
            T1.start();
        } else{
            T1.stopRunning();
            stopSelf();
            stopForeground(true);
        }

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    private void createNotification(String input, PendingIntent pendingIntent) {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("STRapp")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_baseline_person_add_24)
                .setContentIntent(pendingIntent)
                .build();
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
        @Override
        public void run() {
            running = true;
            int counter = 0;
            while(running){
                try {
                    Thread.sleep(1000);
                    Log.d("Log", String.valueOf(counter));
                    counter++;
                    makeOkHTTPReq();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
