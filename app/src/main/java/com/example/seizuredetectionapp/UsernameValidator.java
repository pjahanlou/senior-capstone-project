package com.example.seizuredetectionapp;

import kotlin.text.Regex;

public class UsernameValidator {

    public static boolean validateUsername(String username){
        String noWhiteSpace = "[^-\\s]";

        // checking if the username matches the correct length
        if(username.length() < 8 || username.length() >= 20){
            return false;
        }

        // checking if the username has a special symbol
        if(!username.matches(noWhiteSpace)) {
            return false;
        }

        return true;
    }

}
