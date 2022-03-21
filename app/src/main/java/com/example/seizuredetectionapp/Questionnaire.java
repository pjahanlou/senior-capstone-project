package com.example.seizuredetectionapp;

import android.widget.EditText;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Questionnaire implements Serializable{

    public String name;
    public String contactMethod;
    public String countdownTimer;
    public String dateOfBirth;
    public String seizureT;
    public String sex;
    public static Set<String> addedContacts = new HashSet<>();
    public String seizureDuration, height, weight, seizureFrequencyPerMonth, seizureStart;
    public static Map<String, String> contactMap = new HashMap<>();

    public Questionnaire(){
    }

    // Creates an Object containing the data given by the questionnaire.
    public Questionnaire(String name,
                         Set<String> addedContacts,
                         String countdownTimer,
                         String dateOfBirth,
                         String contactMethod,
                         String seizureDuration,
                         String height,
                         String weight,
                         String seizureFrequencyPerMonth,
                         String seizureStart,
                         String seizureT,
                         String sex,
                         HashMap<String, String> contactMap
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
        this.contactMap = contactMap;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "" + addedContacts.toString() + " " + contactMethod + " " + dateOfBirth + " " + countdownTimer;
    }
}
