package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
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
    private RangeSlider heightSlider, weightSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_personal);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initializing the buttons
        dateOfBirth = findViewById(R.id.dateOfBirthInput);
        submitQuestionnaireButton = findViewById(R.id.submitQuestionairePersonal);
        addContactButton = findViewById(R.id.addContact);

        // Initializing the spinners
        contactMethodSpinner = findViewById(R.id.contactPreferenceSpinner);
        sexSpinner = findViewById(R.id.sexSpinner);

        // Initializing the sliders and EditText
        countdownTimerInput = findViewById(R.id.countdownTimerInput);
        weightSlider = findViewById(R.id.weightSlider);
        heightSlider = findViewById(R.id.heightSlider);

        // Add click listeners to buttons
        dateOfBirth.setOnClickListener(this);
        addContactButton.setOnClickListener(this);
        submitQuestionnaireButton.setOnClickListener(this);

        // Add click listener to countdown timer EditText
        /*
        countdownTimerInput.setOnClickListener(this);

        countdownTimerInput.setInputType(InputType.TYPE_NULL);
        countdownTimerInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snack = Snackbar.make(findViewById(R.id.constraintLayout), "What's good?",
                        Snackbar.LENGTH_SHORT);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snack.show();
            }
        });
        countdownTimerInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Snackbar snack = Snackbar.make(findViewById(R.id.constraintLayout), "What's good?",
                            Snackbar.LENGTH_SHORT);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snack.show();
                }
            }
        });

         */

        // Setting the values of the height slider
        heightSlider.setLabelFormatter(value -> valueToHeight(value));

        // Getting the selected contact method
        contactMethodSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>)
                (oldIndex, oldItem, newIndex, newItem) -> contactMethod = newItem);

        // Getting the selected sex
        sexSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>)
                (oldIndex, oldItem, newIndex, newItem) -> selectedSex = newItem);

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
                        0,
                        this,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
                break;

            case R.id.addContact:
                Intent intent = new Intent(this, ContactsPage.class);
                startActivity(intent);
                break;

            case R.id.submitQuestionairePersonal:
                storeQuestionnaireData();
                break;
        }
    }

    private void storeQuestionnaireData() {
        Log.d("confirmation", "completed list: " + addedContacts);

        String countdownTimer = countdownTimerInput.getText().toString().trim();
        String height = valueToHeight(heightSlider.getValues().get(0));
        String weight = String.valueOf(weightSlider.getValues().get(0));

        // checks to see if any inputs are empty and alerts user.
        if (selectedDOB.equals(null)) {
            dateOfBirth.setError("Age is required!");
            dateOfBirth.requestFocus();
            return;
        }

        if (countdownTimer.isEmpty()) {
            countdownTimerInput.setError("Countdown timer is required!");
            countdownTimerInput.requestFocus();
            return;
        }

        // Saving the fields to local settings
        questionnaireComplete("weight", weight);
        questionnaireComplete("height", height);
        questionnaireComplete("countdown timer", countdownTimer);
        questionnaireComplete("sex", selectedSex);
        questionnaireComplete("age", selectedDOB);
        questionnaireComplete("preferred contact method", contactMethod);

        // Saving the contact list
        localSettings.setContactList(addedContacts);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("contact method", localSettings.getContactList());
        if(editor.commit()){
            Log.d("contacts status", "Successful");
        } else{
            Log.d("contacts status", "Failed");
        }

        // Moving to Questionnaire Medical
        startActivity(new Intent(this, QuestionnaireMedical.class));
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
    
    @Override
    public void onDateSet(DatePicker datePicker,  int year, int month, int dayOfMonth) {
        selectedDOB = (month + 1) + "/" + dayOfMonth + "/" + year;
    }
}