package com.example.seizuredetectionapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

        // add a listener to potentially start alert when receiving server data.

        return null;
    }

    public void WorkManagerInitializer(){

    }

    public void WorkManager(){

    }


}
