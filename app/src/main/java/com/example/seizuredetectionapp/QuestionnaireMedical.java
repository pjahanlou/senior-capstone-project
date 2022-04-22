package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.slider.RangeSlider;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cucumber.deps.com.thoughtworks.xstream.converters.extended.ToStringConverter;

public class QuestionnaireMedical extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    Button openDatePicker;
    String seizureStartD = "", previousActivity;
    private Button submitQuestionnaireMedical;
    private LocalSettings localSettings;
    private NachoTextView seizureTypeView;
    private RangeSlider seizureFreqSlider, averageSeizureDurationSlider, longestSeizureSlider;
    private ImageView hintImage;
    private TextView textBox, titleBox;



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
        hintImage = findViewById(R.id.hintQuestionnaireMedical);


        Set<String> seizureTypes =  settings.getStringSet("seizureType", localSettings.getSeizureTypes());
        Log.d("checkme", String.valueOf(seizureTypes));
        if(seizureTypes != null){
            int n = seizureTypes.size();
            String chipList[] = new String[n];
            int i = 0;
            for (String x : seizureTypes)
                chipList[i++] = x;

            seizureTypeView.setText(Arrays.asList(chipList));
        }
        if(settings.getString("seizureFrequencyPerMonth", "") != ""){
            seizureFreqSlider.setValues(Float.valueOf(settings.getString("seizureFrequencyPerMonth", "")));
        }
        if(settings.getString("seizureDuration", "") != "") {
            Float seizureDuration = averageSeizureReconversion(settings.getString("seizureDuration", ""));
            averageSeizureDurationSlider.setValues(Float.valueOf(seizureDuration));
        }
        if(settings.getString("longestSeizure", "") != "") {
            Float longestSeizure = longestSeizureReconversion(settings.getString("longestSeizure", ""));
            longestSeizureSlider.setValues(Float.valueOf(longestSeizure));
        }

        try{
            previousActivity = getIntent().getExtras().getString("page");

            Log.d("Previous Page: ", ""+previousActivity);
        } catch (Throwable e){
            e.printStackTrace();
        }

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
        submitQuestionnaireMedical.setOnClickListener(this);
        hintImage.setOnClickListener(this);
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

    private Float longestSeizureReconversion(String time){
        if(time.equals("30 Sec")){
            return 0f;
        } else if(time.equals("1 Hour")){
            return 60f;
        }else {
            return Float.parseFloat(time.substring(0, time.indexOf(" Min")));
        }
    }

    private String averageSeizureConvert(float value) {
        if((int) value == 0){
            return "0 sec";
        } else if((int)value == 1){
            return "5 Sec";
        } else if((int)value == 2) {
            return "30 Sec";
        } else {
            return secToMin((int) value);
        }
    }

    private Float averageSeizureReconversion(String time){
        if(time.equals("0 sec")){
            return 0f;
        } else if(time.equals("5 Sec")){
            return 1f;
        } else if(time.equals("30 Sec")){
            return 2f;
        } else {
            return minToSec(time);
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

    public float minToSec(String time){
        Log.d("conversionCheck", time);
        float minutes = Float.parseFloat(time.substring(0, time.indexOf(" Min")));
        float seconds = minutes*60;
        Log.d("conversionCheck", String.valueOf(minutes));
        if(time.contains("Sec")){
            seconds += Float.parseFloat(time.substring(time.indexOf("Min")+3, time.indexOf(" Sec")));
            Log.d("conversionCheck", String.valueOf(seconds));
        }
        return (seconds/30)+1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openDatePickerDialog:
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
            case R.id.submitQuestionnaireMedical:
                saveQuestionnaireMedical();
                break;
            case R.id.hintQuestionnaireMedical:
                showHint(v.getContext());
                break;
        }
    }

    private void saveQuestionnaireMedical() {
        // Getting the values of the views
        Set<String> seizureTypes = new HashSet<>(seizureTypeView.getChipValues());
        String seizureFreq = String.valueOf(seizureFreqSlider.getValues().get(0));
        String averageSeizure = averageSeizureConvert(averageSeizureDurationSlider.getValues().get(0));
        String longestSeizure = longestSeizureConvert(longestSeizureSlider.getValues().get(0));

        if (seizureTypes == null) {
            seizureTypeView.requestFocus();
            seizureTypeView.setError("A seizure type is required!");
            return;
        }

        if (seizureStartD == "") {
            if(previousActivity == null) {
                openDatePicker.requestFocus();
                openDatePicker.setError("A seizure start date is required!");
                return;
            }else if(!previousActivity.equals("AppSettings")){
                openDatePicker.requestFocus();
                openDatePicker.setError("A seizure start date is required!");
                return;
            }
        }

        if (seizureFreq == "0") {
            openDatePicker.requestFocus();
            openDatePicker.setError("A seizure start date is required!");
            return;
        }

        if (averageSeizure == "0") {
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

        // Moving to next page
        if(previousActivity != null){
            if (previousActivity.equals("AppSettings")) {
                finish();
            }
        }else {
            startActivity(new Intent(this, LocationPermission.class));
        }
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

    private void showHint(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_settings_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        textBox = dialog.getWindow().findViewById(R.id.textView2);
        titleBox = dialog.getWindow().findViewById(R.id.textView);
        textBox.setText("By learning about your medical history, the app can better adjust to you.");
        titleBox.setText("Medical Questionnaire");

        Button gotIt = dialog.findViewById(R.id.btn_gotit);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}