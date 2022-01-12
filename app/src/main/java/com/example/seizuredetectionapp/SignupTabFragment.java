package com.example.seizuredetectionapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SignupTabFragment extends Fragment {

    Button signup;
    EditText email, username, password, confirmPassword;
    float v = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        // Getting the views by ID
        email = root.findViewById(R.id.email);
        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.password);
        confirmPassword = root.findViewById(R.id.confirmPassword);
        signup = root.findViewById(R.id.signup);

        // Adding animations to the views
        email.setTranslationY(800);
        username.setTranslationY(800);
        password.setTranslationY(800);
        confirmPassword.setTranslationY(800);
        signup.setTranslationY(800);

        email.setAlpha(v);
        username.setAlpha(v);
        password.setAlpha(v);
        confirmPassword.setAlpha(v);
        signup.setAlpha(v);

        email.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        username.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        password.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        confirmPassword.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();
        signup.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(900).start();

        return root;
    }

}
