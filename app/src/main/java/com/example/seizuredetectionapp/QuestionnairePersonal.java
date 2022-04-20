package com.example.seizuredetectionapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class QuestionnairePersonal extends AppCompatActivity implements View.OnClickListener, Serializable, DatePickerDialog.OnDateSetListener {
    public EditText countdownTimerInput;
    public Button submitQuestionnaireButton, addContactButton, dateOfBirth;
    public PowerSpinnerView sexSpinner, contactMethodSpinner;
    public String selectedDOB;
    public LocalSettings localSettings;
    public Set<String> listOfContacts = new HashSet<>();
    public String contactMethod, selectedSex;
    private RangeSlider heightSlider, weightSlider, countdownTimerSlider;
    private ImageView hintImageCountdownTimer, hintImageQuestionnairePersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_personal);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initializing the buttons
        dateOfBirth = findViewById(R.id.dateOfBirthInput);
        submitQuestionnaireButton = findViewById(R.id.submitQuestionairePersonal);
        addContactButton = findViewById(R.id.addContact);
        hintImageCountdownTimer = findViewById(R.id.hint);

        // Initializing the spinners
        contactMethodSpinner = findViewById(R.id.contactPreferenceSpinner);
        sexSpinner = findViewById(R.id.sexSpinner);

        // Initializing the sliders and EditText
        countdownTimerSlider = findViewById(R.id.countdownTimerInput);
        weightSlider = findViewById(R.id.weightSlider);
        heightSlider = findViewById(R.id.heightSlider);
        hintImageQuestionnairePersonal = findViewById(R.id.hintQuestionnairePersonal);

        // Initializing the hint view

        // Add click listeners to buttons
        dateOfBirth.setOnClickListener(this);
        addContactButton.setOnClickListener(this);
        submitQuestionnaireButton.setOnClickListener(this);
        hintImageCountdownTimer.setOnClickListener(this);
        hintImageQuestionnairePersonal.setOnClickListener(this);

        // Setting the values for Countdown timer slider
        countdownTimerSlider.setLabelFormatter(value -> countdownTimerFormatter(value));

        // Setting the values of the height slider
        heightSlider.setLabelFormatter(value -> valueToHeight(value));

        // Getting the selected contact method
        contactMethodSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>)
                (oldIndex, oldItem, newIndex, newItem) -> contactMethod = newItem);

        // Getting the selected sex
        sexSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>)
                (oldIndex, oldItem, newIndex, newItem) -> selectedSex = newItem);

    }

    public String countdownTimerFormatter(float value){
        if(value == 60){
            return "1 Min";
        }
        return (int) value +" sec";
    }

    /**
     * Method for converting inches to height values
     * */
    public String valueToHeight(float value){
        int inches = (int)value + 46;
        int feet = inches / 12;
        int inch = inches % 12;
        return String.valueOf(feet)+"'"+String.valueOf(inch)+"";
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.dateOfBirthInput:
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        R.style.datepicker_theme,
                        this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
                break;

            case R.id.addContact:
                Intent intent = new Intent(this, ContactsPage.class);
                intent.putExtra("settings page", false);
                startActivity(intent);
                break;

            case R.id.submitQuestionairePersonal:
                storeQuestionnaireData();
                break;
            case R.id.hint:
                showHint();
                break;

        }
    }

    private void storeQuestionnaireData() {

        String countdownTimer = String.valueOf(Math.round(countdownTimerSlider.getValues().get(0)));
        String height = valueToHeight(heightSlider.getValues().get(0));
        String weight = String.valueOf(weightSlider.getValues().get(0));

        // checks to see if any inputs are empty and alerts user.
        if (selectedDOB == null) {
            dateOfBirth.setError("Age is required!");
            dateOfBirth.requestFocus();
            return;
        }

        // Saving the fields to local settings
        questionnaireComplete("weight", weight);
        questionnaireComplete("height", height);
        questionnaireComplete("countdown timer", countdownTimer);
        questionnaireComplete("sex", selectedSex);
        questionnaireComplete("age", selectedDOB);
        questionnaireComplete("preferred contact method", contactMethod);

        // Moving to Questionnaire Medical
        Intent intent = getIntent();
        try {
            String previousActivity = intent.getStringExtra("PreviousActivity");
            if (previousActivity.equals("AppSettings")) {
                finish();
            } else {
                startActivity(new Intent(this, QuestionnaireMedical.class));
            }
        } catch(Exception e){
            startActivity(new Intent(this, QuestionnaireMedical.class));
        }
    }

    private void questionnaireComplete(String field, String value){
        localSettings.setField(field, value);

        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(field, localSettings.getField(field));

        if(editor.commit()){
            Log.d(field.concat(" status"), "Successful");
        } else{
            Log.d(field.concat(" status"), "Failed");
        }
    }

    /**
     * method for displaying the new user dialog
     */
    private void showHint() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.countdown_timer_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button gotIt = dialog.findViewById(R.id.btn_gotit);
      
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker,  int year, int month, int dayOfMonth) {
        selectedDOB = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}