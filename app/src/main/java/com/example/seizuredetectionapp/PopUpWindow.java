package com.example.seizuredetectionapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PopUpWindow extends AppCompatActivity {
    private TextView recieve_contacts;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText popup_name, popup_countdown, Input_emergency_contacts;
    private Button Questionnaire, Submit, AddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        Input_emergency_contacts = (EditText) findViewById(R.id.Emergency_Contact_Input);
        recieve_contacts = (TextView) findViewById(R.id.Contact_View);
        AddContact = (Button) findViewById(R.id.Add_Contact);

        AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=Input_emergency_contacts.getText().toString();
                recieve_contacts.append(name + "\n");
            }
        });
    }
}