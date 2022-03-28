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
        Context applicationContext = getApplicationContext();
        return null;
    }

    public void WorkManagerInitializer(){

    }

    public void WorkManager(){

    }


}
