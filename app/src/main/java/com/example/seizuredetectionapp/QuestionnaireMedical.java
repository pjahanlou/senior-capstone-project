package com.example.seizuredetectionapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hootsuite.nachos.NachoTextView;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import java.util.Calendar;
import java.util.Set;


public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    NumberPicker seizureDurationMinutes, seizureDurationSeconds, heightFeet, heightInches;
    EditText seizureFrequency, weightInput;
    Spinner seizureType, sexInput;
    Button openDatePicker;
    String seizureStartD;
    private TransitionButton submitQuestionnaireMedical;
    private LocalSettings localSettings;
    private NachoTextView nachoTextView;
    private RangeSlider averageSeizureDurationSlider, longestSeizureSlider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_medical);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //seizureDurationMinutes = findViewById(R.id.seizureDurationMinutes);
        //seizureDurationSeconds = findViewById(R.id.seizureDurationSeconds);
        //heightFeet = findViewById(R.id.heightInputFeet);
        //heightInches = findViewById(R.id.heightInputInches);
        //weightInput = findViewById(R.id.weightInput);
        //seizureFrequency = findViewById(R.id.seizureFrequency);
        //seizureType = findViewById(R.id.seizureType);
        //sexInput = findViewById(R.id.sexInput);
        nachoTextView = findViewById(R.id.nacho_text_view);
        averageSeizureDurationSlider = findViewById(R.id.averageSeizureDurationSlider);
        longestSeizureSlider = findViewById(R.id.longestSeizureSlider);
        seizureStartD = "";

        averageSeizureDurationSlider.setLabelFormatter(value -> {
            if(value == 1){
                return "5 Sec";
            } else if(value == 2) {
                return "30 Sec";
            } else {
                return secToMin((int) value);
            }
        });

        longestSeizureSlider.setLabelFormatter(value -> {
            if(value == 0){
                return "30 Sec";
            } else if(value == 60) {
                return "1 Hour";
            } else {
                return ((int)value)+" Min";
            }
        });


        String[] suggestions = new String[]{"Generalized tonic-clonic (GTC)"
                ,"Tonic"
                ,"Clonic"
                ,"Absence"
                ,"Myoclonic"
                ,"Atonic"
                ,"Infantile or Epileptic spasms"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        nachoTextView.setAdapter(adapter);

        openDatePicker = findViewById(R.id.openDatePickerDialog);
        submitQuestionnaireMedical = findViewById(R.id.submitQuestionnaireMedical);

        openDatePicker.setOnClickListener(this);
        submitQuestionnaireMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                submitQuestionnaireMedical.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = true;

                        // Choose a stop animation if your call was succesful or not
                        if (isSuccessful) {
                            submitQuestionnaireMedical.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    Intent intent = new Intent(getBaseContext(), Navbar.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            submitQuestionnaireMedical.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }

    public String secToMin(int seconds){
        int time = (seconds - 1) * 30;
        int minute = time / 60;
        int secs = time % 60;
        if (secs == 0){
            return String.valueOf(minute)+" Min";
        }
        return String.valueOf(minute)+" Min "+String.valueOf(secs)+" Sec";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openDatePickerDialog: {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        0,
                        this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();

                break;
            }
            
            case R.id.submitQuestionnaireMedical:
                saveQuestionnaireMedicalToFirebase();
                break;
        }
    }

    private void saveQuestionnaireMedicalToFirebase() {
        //String seizureDuration = String.valueOf(seizureDurationSeconds.getValue() + (seizureDurationMinutes.getValue() * 60));
        //String height = String.valueOf(heightInches.getValue() + (heightFeet.getValue() * 12));
        //String weight = weightInput.getText().toString().trim();
        //String seizureFrequencyPerMonth = seizureFrequency.getText().toString().trim();
        //String seizureT = seizureType.getSelectedItem().toString().trim();
        //String sex = sexInput.getSelectedItem().toString().trim();

        //checks to see if any inputs are empty and alerts user.
        //if (seizureDuration.equals("0")) {
        //    seizureDurationSeconds.requestFocus();
        //    return;
        //}
        /*
        if (height.equals("0")) {
            heightFeet.requestFocus();
            return;
        }

        if (weight.equals("0")) {
            weightInput.requestFocus();
            return;
        }

        if (seizureT.equals("0")) {
            seizureType.requestFocus();
            return;
        }

         */

        //if (seizureStartD == "") {
        //    openDatePicker.requestFocus();
        //    openDatePicker.setError("A seizure start date is required!");
        //    return;
        //}

        // grab data from last questionnaire
        //localSettings.setSeizureDuration(seizureDuration);
        //localSettings.setHeight(height);
        //localSettings.setWeight(weight);
        //localSettings.setSeizureFrequency(seizureFrequencyPerMonth);
        //questionnaireComplete();
        
    }

    private void questionnaireComplete(){
        /*
        localSettings.setQuestionnaireComplete("1");

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(LocalSettings.DEFAULT, localSettings.getSeizureDuration());
        editor.apply();
        editor.putString(LocalSettings.DEFAULT, localSettings.getHeight());
        editor.apply();
        editor.putString(LocalSettings.DEFAULT, localSettings.getWeight());
        editor.apply();
        editor.putString(LocalSettings.DEFAULT, localSettings.getSeizureFrequency());
        editor.apply();
        editor.putString(LocalSettings.DEFAULT, localSettings.getQuestionnaireComplete());
        editor.apply();

        Log.d("Local Storage", "" + localSettings.getCountdownTimer());
        startActivity(new Intent(QuestionnaireMedical.this, Navbar.class));

         */
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        seizureStartD = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}