package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Questionnaire implements Serializable{
    public String name, contactMethod, countdownTimer, age, seizureStartM, seizureT, sex;
    public ArrayList<String> contactList;
    public String seizureDuration, height, weight, seizureFrequencyPerMonth, seizureStartD, seizureStartY;

    public Questionnaire(){
    }

    // Creates an Object containing the data given by the questionnaire.
    public Questionnaire(String name,
                         ArrayList<String> contactList,
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
        this.contactList = contactList;
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
        return name + contactList.toString() + ' ' + contactMethod + ' ' + age + ' ' + countdownTimer;
    }
}
