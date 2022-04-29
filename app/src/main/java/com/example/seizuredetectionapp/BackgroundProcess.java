package com.example.seizuredetectionapp;

import static java.security.AccessController.getContext;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.SynchronousExecutor;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;

public class BackgroundProcess extends Worker {
    private BluetoothLeScanner mBluetoothLeScanner;

    public void onCreate(){
        WorkManagerInitializer();
    }

    public BackgroundProcess(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        // bluetooth setup goes here
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        Log.d("Checkpoint 1", "Reached");
        //mBluetoothLeScanner.startScan();
        // Request data from wearable

        // add a listener to send data to server when receiving wearable data.
        requestSeizureCheck(getContext());
        // send data to live charts

        // add a listener to potentially start alert when receiving server data.

        return Result.success();
    }

    public void WorkManagerInitializer(){
        WorkManager();
    }

    public void WorkManager(){
        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(BackgroundProcess.class, 1, TimeUnit.MINUTES)
                        // Constraints
                        .build();

    }

    public void requestSeizureCheck(AccessControlContext context){
        JSONObject myData = new JSONObject();

        JsonObjectRequest jsonObjectSend = new JsonObjectRequest
            (HTTPHelpers.MYURL, myData ,null, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        //get data from backend server
        JsonObjectRequest jsonObjectReceive = new JsonObjectRequest
            (Request.Method.POST, HTTPHelpers.MYURL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //if(response.hasSeizure == true){
                        //start countdown or alert_page or whatever.
                    //}
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error

                }
            });
    }
}
