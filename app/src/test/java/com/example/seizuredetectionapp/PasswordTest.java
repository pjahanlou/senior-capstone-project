package com.example.seizuredetectionapp;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.google.common.truth.Truth;

public class PasswordTest {

    // Unit Tests
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