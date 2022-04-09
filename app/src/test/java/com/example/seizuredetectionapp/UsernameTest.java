package com.example.seizuredetectionapp;

import com.google.common.truth.Truth;
import org.junit.Test;

public class UsernameTest {

    // Unit Tests
    @Test
    public void whenUsernameNotInRange(){
        String username = "STRapp";
        boolean result = UsernameValidator.validateUsername(username);
        Truth.assertThat(result).isTrue();
    }

    @Test
    public void whenUsernameContainsWhiteSpace(){
        String username = "STRapp is cool";
        boolean result = UsernameValidator.validateUsername(username);
        Truth.assertThat(result).isTrue();
    }

    @Test
    public void whenUsernameCorrect(){
        String username = "SeizureApp";
        boolean result = UsernameValidator.validateUsername(username);
        Truth.assertThat(result).isFalse();
    }
}
