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
    public String age;
    public String seizureStartM;
    public String seizureT;
    public String sex;
    public static ArrayList<String> addedContacts;
    public String seizureDuration, height, weight, seizureFrequencyPerMonth, seizureStartD, seizureStartY;

    public Questionnaire(){
    }

    // Creates an Object containing the data given by the questionnaire.
    public Questionnaire(String name,
                         ArrayList<String> addedContacts,
                         String countdownTimer,
                         String age,
                         String contactMethod,
                         String seizureDuration,
                         String height,
                         String weight,
                         String seizureFrequencyPerMonth,
                         String seizureStartD,
                         String seizureStartM,
                         String seizureStartY,
                         String seizureT,
                         String sex
            ){
        this.name = name;
        this.addedContacts = addedContacts;
        this.countdownTimer = countdownTimer;
        this.age = age;
        this.contactMethod = contactMethod;
        this.seizureDuration = seizureDuration;
        this.height = height;
        this.weight = weight;
        this.seizureFrequencyPerMonth = seizureFrequencyPerMonth;
        this.seizureStartD = seizureStartD;
        this.seizureStartM = seizureStartM;
        this.seizureStartY = seizureStartY;
        this.seizureT = seizureT;
        this.sex = sex;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "" + addedContacts.toString() + " " + contactMethod + " " + age + " " + countdownTimer;
    }
}
