package com.example.seizuredetectionapp;

import org.junit.Test;

import com.google.common.truth.Truth;

public class PasswordTest {

    @Test
    public void whenPasswordIsValid(){
        String username = "Parsa@1379";
        boolean result = PasswordValidator.validatePassword(username);
        Truth.assertThat(result).isTrue();
    }

    @Test
    public void whenPasswordIsNotTheCorrectLength(){
        String username = "parsa";
        boolean result = PasswordValidator.validatePassword(username);
        Truth.assertThat(result).isTrue();
    }

    @Test
    public void whenPasswordDoesNotContainSpecialSymbol(){
        String username = "Parsa1379";
        boolean result = PasswordValidator.validatePassword(username);
        Truth.assertThat(result).isTrue();
    }

    @Test
    public void whenPasswordDoesNotContainNumber(){
        String username = "parsajahanlou@";
        boolean result = PasswordValidator.validatePassword(username);
        Truth.assertThat(result).isTrue();
    }

}