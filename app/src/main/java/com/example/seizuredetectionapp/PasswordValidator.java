package com.example.seizuredetectionapp;

import android.util.Patterns;

class PasswordValidator {

    public static boolean validatePassword(String password){
        String specialChars = "(.*[@,#,$,%].*$)";
        String numbers = "(.*[0-9].*)";

        // checking if the password matches the correct length
        if(password.length() < 8 || password.length() >= 20){
            return false;
        }

        // checking if the password has a special symbol
        if(!password.matches(specialChars)){
            return false;
        }

        // checking if the password contains a number
        if(!password.matches(numbers)){
            return false;
        }

        return true;
    }
}
