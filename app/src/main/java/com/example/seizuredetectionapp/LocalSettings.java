package com.example.seizuredetectionapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.Set;

public class LocalSettings extends Application {

    public static String PREFERENCES = "preferences";
    public static String DEFAULT = "default";
    public static String countdownTimer;
    public static String age;
    public static String seizureDuration;
    public static String height;
    public static String weight;
    public static String seizureFrequency;
    public static String questionnaireComplete;
    public static Set<String> contactList;
    public static String preferredContactMethod;

    // A wrapper function for the app settings activity
    public static String getField(String field) {
        String fieldValue = null;

        switch(field){
            case "name":
                fieldValue = getName();
                break;
            case "countdownTimer":
                fieldValue = getCountdownTimer();
                break;
            case "age":
                fieldValue = getAge();
                break;
            case "seizureDuration":
                fieldValue = getSeizureDuration();
                break;
            case "height":
                fieldValue = getHeight();
                break;
            case "weight":
                fieldValue = getWeight();
                break;
            case "seizureFrequencyPerMonth":
                fieldValue = getSeizureFrequency();
                break;
        }

        return fieldValue;
    }

    // A wrapper function for the app settings activity
    public static void setField(String field, String value) {
        switch(field){
            case "name":
                setName(value);
                break;
            case "countdownTimer":
                setCountdownTimer(value);
                break;
            case "age":
                setAge(value);
                break;
            case "seizureDuration":
                setSeizureDuration(value);
                break;
            case "height":
                setHeight(value);
                break;
            case "weight":
                setWeight(value);
                break;
            case "seizureFrequencyPerMonth":
                setSeizureFrequency(value);
                break;
        }
    }

    public static String getCountdownTimer() {
        return countdownTimer;
    }

    public static void setCountdownTimer(String countdownTimer) {
        LocalSettings.countdownTimer = countdownTimer;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        LocalSettings.name = name;
    }

    public static String name;

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        LocalSettings.age = age;
    }

    public static String getSeizureDuration() {
        return seizureDuration;
    }

    public static void setSeizureDuration(String seizureDuration) {
        LocalSettings.seizureDuration = seizureDuration;
    }

    public static String getHeight() {
        return height;
    }

    public static void setHeight(String height) {
        LocalSettings.height = height;
    }

    public static String getWeight() {
        return weight;
    }

    public static void setWeight(String weight) {
        LocalSettings.weight = weight;
    }

    public static String getSeizureFrequency() {
        return seizureFrequency;
    }

    public static void setSeizureFrequency(String seizureFrequency) {
        LocalSettings.seizureFrequency = seizureFrequency;
    }

    public static String getQuestionnaireComplete() {
        return questionnaireComplete;
    }

    public static void setQuestionnaireComplete(String questionnaireComplete) {
        LocalSettings.questionnaireComplete = questionnaireComplete;
    }

    public static Set<String> getContactList() {
        return contactList;
    }

    public static void setContactList(Set<String> contactList) {
        LocalSettings.contactList = contactList;
    }

    public static String getPreferredContactMethod() {
        return preferredContactMethod;
    }

    public static void setPreferredContactMethod(String preferredContactMethod) {
        LocalSettings.preferredContactMethod = preferredContactMethod;
    }
}
