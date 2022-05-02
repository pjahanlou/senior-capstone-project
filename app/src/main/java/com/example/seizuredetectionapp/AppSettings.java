package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seizuredetectionapp.adapter.DiscoveredBluetoothDevice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class AppSettings extends AppCompatActivity implements View.OnClickListener{
    private Button changePersonalQuestionnaire, changeContactList, closeActivity,
            changeMedicalQuestionnaire, changeUsualLocations, connectToWearable;
    private Switch PrivacyMode;

    private String currentUserUID;
    private FirebaseDatabase database;
    private DatabaseReference settingsTable;

    private LocalSettings localSettings;

    private Questionnaire settings = new Questionnaire();
    private Activity QuestionnaireMedical;

//    public static final String EXTRA_DEVICE = "com.example.seizuredetectionapp.EXTRA_DEVICE";
//    public static DiscoveredBluetoothDevice device;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        // initializing the buttons
        closeActivity = findViewById(R.id.back);
        changeMedicalQuestionnaire = findViewById(R.id.startMedicalQuestionnaire);
        changePersonalQuestionnaire = findViewById(R.id.startPersonalQuestionnaire);
        changeContactList = findViewById(R.id.changeContactList);
        changeUsualLocations = findViewById(R.id.changeUsualLocations);
        connectToWearable = findViewById(R.id.connectToWearable);
        
        // Adding event listeners to the buttons and dropdowns
        closeActivity.setOnClickListener(this);
        changeMedicalQuestionnaire.setOnClickListener(this);
        changePersonalQuestionnaire.setOnClickListener(this);
        changeContactList.setOnClickListener(this);
        changeUsualLocations.setOnClickListener(this);
        connectToWearable.setOnClickListener(this);
//        Log.d("EXTRA_DEVICE", EXTRA_DEVICE);
//        Log.d("device", device.toString());
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                Intent intent = new Intent(this, Navbar.class);
                intent.putExtra ("go to settings", true);
                startActivity (intent);
                break;
            case R.id.startMedicalQuestionnaire:
                intent = new Intent(this, QuestionnaireMedical.class);
                intent.putExtra("page", "AppSettings");
                startActivity(intent);
                break;
            case R.id.startPersonalQuestionnaire:
                intent = new Intent(this, QuestionnairePersonal.class);
                intent.putExtra("page", "AppSettings");
                startActivity(intent);
                break;
            case R.id.changeUsualLocations:
                intent = new Intent(this, UsualLocations.class);
                intent.putExtra("page", "AppSettings");
                startActivity(intent);
                break;
            case R.id.changeContactList:
                intent = new Intent(this, UpdateContacts.class);
                intent.putExtra("page", "AppSettings");
                startActivity(intent);
                break;
            case R.id.connectToWearable:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
        }
    }
}