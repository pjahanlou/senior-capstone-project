package com.example.seizuredetectionapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LocalSettings extends Application {

    public static String PREFERENCES = "preferences";
    public static String DEFAULT = "default";
    public static String countdownTimer;
    public static String age;
    public static String seizureDuration;
    public static String longestSeizure;
    public static String height;
    public static String weight;
    public static String seizureFrequency;
    public static String questionnaireComplete;
    public static Set<String> contactList = new HashSet<>();
    public static Set<String> seizureTypes = new HashSet<>();
    public static Set<String> locations = new HashSet<>();
    public static String preferredContactMethod;
    public static String sex;
    public static String firstSeizureDate;

    // A wrapper function for the app settings activity
    public static String getField(String field) {
        String fieldValue = null;

        switch(field){
            case "name":
                fieldValue = getName();
                break;
            case "countdown timer":
                fieldValue = getCountdownTimer();
                break;
            case "age":
                fieldValue = getAge();
                break;
            case "sex":
                fieldValue = getSex();
                break;
            case "seizureDuration":
                fieldValue = getSeizureDuration();
                break;
            case "longestSeizure":
                fieldValue = getLongestSeizure();
                break;
            case "questionnaire bool":
                fieldValue = getQuestionnaireComplete();
                break;
            case "firstSeizure":
                fieldValue = getFirstSeizureDate();
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
            case "preferred contact method":
                fieldValue = getPreferredContactMethod();
        }

        return fieldValue;
    }

    // A wrapper function for the app settings activity
    public static void setField(String field, String value) {
        switch(field){
            case "name":
                setName(value);
                break;
            case "countdown timer":
                setCountdownTimer(value);
                break;
            case "age":
                setAge(value);
                break;
            case "sex":
                setSex(value);
                break;
            case "seizureDuration":
                setSeizureDuration(value);
                break;
            case "questionnaire bool":
                setQuestionnaireComplete(value);
                break;
            case "longestSeizure":
                setLongestSeizure(value);
                break;
            case "firstSeizure":
                setFirstSeizureDate(value);
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
            case "preferred contact method":
                setPreferredContactMethod(value);
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

    public static String getSex() {
        return sex;
    }

    public static void setSex(String sex) {
        LocalSettings.sex = sex;
    }

    public static String getLongestSeizure() {
        return longestSeizure;
    }

    public static void setLongestSeizure(String longestSeizure) {
        LocalSettings.longestSeizure = longestSeizure;
    }

    public static String getFirstSeizureDate() {
        return firstSeizureDate;
    }

    public static void setFirstSeizureDate(String firstSeizureDate) {
        LocalSettings.firstSeizureDate = firstSeizureDate;
    }

    public static Set<String> getSeizureTypes() {
        return seizureTypes;
    }

    public static void setSeizureTypes(Set<String> seizureTypes) {
        LocalSettings.seizureTypes = seizureTypes;
    }

    public static Set<String> getLocations() {
        return locations;
    }

    public static void setLocations(Set<String> locations) {
        LocalSettings.locations = locations;
    }
}
