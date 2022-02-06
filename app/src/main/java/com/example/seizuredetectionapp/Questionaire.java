package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Questionaire extends AppCompatActivity {
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);

        start = (Button) findViewById(R.id.startQuestionaire);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent popupwindow = new Intent( Questionaire.this, QuestionnairePersonal.class);
                startActivity(popupwindow);
            }
        });
    }
}