package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Questionnaire {
    public String name, contactMethod, countdownTimer, age;
    public ArrayList<String> contactList;

    public Questionnaire(){

    }

    public Questionnaire(String name, ArrayList<String> contactList, String countdownTimer, String age, String contactMethod){
        this.name = name;
        this.contactList = contactList;
        this.countdownTimer = countdownTimer;
        this.age = age;
        this.contactMethod = contactMethod;
    }

    @NonNull
    @Override
    public String toString() {
        return name + contactList.toString() + ' ' + contactMethod + ' ' + age + ' ' + countdownTimer;
    }
}
