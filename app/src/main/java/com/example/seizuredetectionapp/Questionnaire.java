package com.example.seizuredetectionapp;

import android.widget.EditText;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Questionnaire implements Serializable{

    public String name;
    public String contactMethod;
    public String countdownTimer;
    public String dateOfBirth;
    public String seizureT;
    public String sex;
    public static ArrayList<String> addedContacts;
    public String seizureDuration, height, weight, seizureFrequencyPerMonth, seizureStart;

    public Questionnaire(){
    }

    // Creates an Object containing the data given by the questionnaire.
    public Questionnaire(String name,
                         ArrayList<String> addedContacts,
                         String countdownTimer,
                         String dateOfBirth,
                         String contactMethod,
                         String seizureDuration,
                         String height,
                         String weight,
                         String seizureFrequencyPerMonth,
                         String seizureStart,
                         String seizureT,
                         String sex
            ){
        this.name = name;
        this.addedContacts = addedContacts;
        this.countdownTimer = countdownTimer;
        this.dateOfBirth = dateOfBirth;
        this.contactMethod = contactMethod;
        this.seizureDuration = seizureDuration;
        this.height = height;
        this.weight = weight;
        this.seizureFrequencyPerMonth = seizureFrequencyPerMonth;
        this.seizureStart = seizureStart;
        this.seizureT = seizureT;
        this.sex = sex;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "" + addedContacts.toString() + " " + contactMethod + " " + dateOfBirth + " " + countdownTimer;
    }
}
