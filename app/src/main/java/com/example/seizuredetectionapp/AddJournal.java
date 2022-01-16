package com.example.seizuredetectionapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

public class AddJournal extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addjournal);

        Button btnclosewindow = (Button) findViewById(R.id.btnclose);

        btnclosewindow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //closes activity and returns to datatable
                //replace with saving data to DB and display on datatable activity
                finish();
            }
        });
    }


}
