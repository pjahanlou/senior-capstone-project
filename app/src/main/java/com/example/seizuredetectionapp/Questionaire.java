package com.example.seizuredetectionapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Questionaire extends AppCompatActivity {
    private TextView recieve_contacts;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popup_name, popup_countdown, Input_emergency_contacts;
    private Button Questionnaire, Submit, AddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);
        Intent popupwindow = new Intent( Questionaire.this, PopUpWindow.class);
        startActivity(popupwindow);
    }
}