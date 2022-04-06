package com.example.seizuredetectionapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

import java.security.AccessController;

public class BackgroundProcess extends Worker {

    public BackgroundProcess(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // may or may not be needed:
        // Context applicationContext = getApplicationContext();

        // Request data from wearable on regular intervals

        // add a listener to send data to server when receiving wearable data.
        // send data to live charts

        // add a listener to potentially start alert when receiving server data.

        return null;
    }

    public void WorkManagerInitializer(){
        //Start Process when bluetooth connects (call doWork())
    }

    public void WorkManager(){
        //repeat doWork() every x seconds
    }

    public void requestSeizureCheck(Context context){
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
                        if(response.hasSeizure == true){
                            //start countdown or alert_page or whatever.
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        //probably not useful but eh.
        /*RequestQueue queue = Volley.newRequestQueue(context);
        queue.start();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, HTTPHelpers.MYURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Add response key to firebase
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("userkey").setValue(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        });

        queue.add(stringRequest);*/

    }
}
