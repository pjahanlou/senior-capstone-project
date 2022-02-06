package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Questionnaire implements Serializable{
    public String name, contactMethod, countdownTimer, age, seizureStartM, seizureT, sex;
    public ArrayList<String> contactList;
    public int seizureDuration, height, weight, seizureFrequencyPerMonth, seizureStartD, seizureStartY;

    public Questionnaire(){
    }

    // Creates an Object containing the data given by the questionnaire.
    public Questionnaire(String name,
                         ArrayList<String> contactList,
                         String countdownTimer,
                         String age,
                         String contactMethod,
                         Integer seizureDuration,
                         Object height,
                         Object weight,
                         Object seizureFrequencyPerMonth,
                         Object seizureStartD,
                         String seizureStartM,
                         Object seizureStartY,
                         String seizureT,
                         String sex
            ){
        this.name = name;
        this.contactList = contactList;
        this.countdownTimer = countdownTimer;
        this.age = age;
        this.contactMethod = contactMethod;
        this.seizureDuration = seizureDuration;
        this.height = (int) height;
        this.weight = (int) weight;
        this.seizureFrequencyPerMonth = (int) seizureFrequencyPerMonth;
        this.seizureStartD = (int) seizureStartD;
        this.seizureStartM = seizureStartM;
        this.seizureStartY = (int) seizureStartY;
        this.seizureT = seizureT;
        this.sex = sex;


    }

    @NonNull
    @Override
    public String toString() {
        return name + contactList.toString() + ' ' + contactMethod + ' ' + age + ' ' + countdownTimer;
    }
}
