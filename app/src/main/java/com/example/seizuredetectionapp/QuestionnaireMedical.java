package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.slider.RangeSlider;
import com.hootsuite.nachos.NachoTextView;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    Button openDatePicker;
    String seizureStartD = "";
    private TransitionButton submitQuestionnaireMedical;
    private LocalSettings localSettings;
    private NachoTextView seizureTypeView;
    private RangeSlider seizureFreqSlider, averageSeizureDurationSlider, longestSeizureSlider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_medical);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Logging the personal questionnaire data
        SharedPreferences settings = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
        Log.d("height", ""+settings.getString("height", ""));
        Log.d("weight", ""+settings.getString("weight", ""));
        Log.d("sex", ""+settings.getString("sex", ""));
        Log.d("countdown timer", ""+settings.getString("countdown timer", ""));
        Log.d("age", ""+settings.getString("age", ""));
        Log.d("contact list", ""+settings.getStringSet("contact method", localSettings.getContactList()));
        Log.d("pref contact method", ""+settings.getString("preferred contact method", ""));

        seizureTypeView = findViewById(R.id.nacho_text_view);
        seizureFreqSlider = findViewById(R.id.seizureFrequencySlider);
        averageSeizureDurationSlider = findViewById(R.id.averageSeizureDurationSlider);
        longestSeizureSlider = findViewById(R.id.longestSeizureSlider);
        openDatePicker = findViewById(R.id.openDatePickerDialog);
        submitQuestionnaireMedical = findViewById(R.id.submitQuestionnaireMedical);

        // Formatting the average seizure slider
        averageSeizureDurationSlider.setLabelFormatter(value -> {
            return averageSeizureConvert(value);
        });

        // Formatting the longest seizure slider
        longestSeizureSlider.setLabelFormatter(value -> {
            return longestSeizureConvert(value);
        });

        // Populating the seizure type nacho textview
        String[] suggestions = new String[]{"Generalized tonic-clonic (GTC)"
                ,"Tonic"
                ,"Clonic"
                ,"Absence"
                ,"Myoclonic"
                ,"Atonic"
                ,"Infantile or Epileptic spasms"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        seizureTypeView.setAdapter(adapter);

        openDatePicker.setOnClickListener(this);

        // Cool animation for the submit button
        submitQuestionnaireMedical.setOnClickListener(v -> {
            // Start the loading animation when the user tap the button
            submitQuestionnaireMedical.startAnimation();

            // Do your networking task or background work here.
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                boolean isSuccessful = true;
                saveQuestionnaireMedical();

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
            }, 1000);
        });
    }

    private String longestSeizureConvert(float value) {
        if(value == 0){
            return "30 Sec";
        } else if(value == 60) {
            return "1 Hour";
        } else {
            return ((int)value)+" Min";
        }
    }

    private String averageSeizureConvert(float value) {
        if((int)value == 1){
            return "5 Sec";
        } else if((int)value == 2) {
            return "30 Sec";
        } else {
            return secToMin((int) value);
        }
    }

    /**
     * This method converts seconds to minutes and seconds
     * */
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
        }
    }

    private void saveQuestionnaireMedical() {
        // Getting the values of the views
        Set<String> seizureTypes = new HashSet<>(seizureTypeView.getChipValues());
        String seizureFreq = String.valueOf(seizureFreqSlider.getValues().get(0));
        String averageSeizure = averageSeizureConvert(averageSeizureDurationSlider.getValues().get(0));
        String longestSeizure = longestSeizureConvert(longestSeizureSlider.getValues().get(0));

        if (seizureStartD == "") {
            openDatePicker.requestFocus();
            openDatePicker.setError("A seizure start date is required!");
            return;
        }

        // Writing to local settings
        questionnaireComplete("seizureFrequencyPerMonth", seizureFreq);
        questionnaireComplete("seizureDuration", averageSeizure);
        questionnaireComplete("longestSeizure", longestSeizure);
        questionnaireComplete("firstSeizure", seizureStartD);

        localSettings.setSeizureTypes(seizureTypes);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("seizureTypes", localSettings.getSeizureTypes());
        if(editor.commit()){
            Log.d("seizureTypes", "Successful");
        } else{
            Log.d("seizureTypes", "Failed");
        }

        questionnaireComplete("questionnaire bool", "1");

        // Moving to Datatable fragment
        startActivity(new Intent(this, Navbar.class));
        
    }

    private void questionnaireComplete(String field, String value){
        localSettings.setField(field, value);

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(field, localSettings.getField(field));

        if(editor.commit()){
            Log.d(field, "Successful");
        } else{
            Log.d(field, "Failed");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        seizureStartD = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}