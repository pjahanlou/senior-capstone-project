package com.example.seizuredetectionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignupTabFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    Button signup;
    EditText email, username, password, confirmPassword;
    float v = 0;
    static boolean signupFlag = false;
    private LocalSettings localSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        // Initializing Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Getting the views by ID
        email = root.findViewById(R.id.email);
        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.password);
        confirmPassword = root.findViewById(R.id.confirmPassword);
        signup = root.findViewById(R.id.signup);
        signup.setOnClickListener(this);

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

    // Overriding the onclick function to signup the user and move to the datatable page
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.signup:
                // Change to the Questionnaire page
                boolean flag = signupUser();
                break;
        }
    }

    // Checking the user inputs and signing them up if valid
    public boolean signupUser(){
        String emailText = email.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        // Regex strings for password confirmation
        String upperCaseChars = "(.*[A-Z].*)";
        String lowerCaseChars = "(.*[a-z].*)";
        String numbers = "(.*[0-9].*)";
        String specialChars = "(.*[@,#,$,%].*$)";

        if(emailText.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return signupFlag;
        }

        if(usernameText.isEmpty()){
            username.setError("Username is required!");
            username.requestFocus();
            return signupFlag;
        }

        if(passwordText.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return signupFlag;
        }

        if(confirmPasswordText.isEmpty()){
            confirmPassword.setError("Confirm password is required!");
            confirmPassword.requestFocus();
            return signupFlag;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Please provide a valid email address");
            email.requestFocus();
            return signupFlag;
        }

        if(passwordText.length() < 8 || passwordText.length() > 20){
            password.setError("The password needs to be at between 8-20 characters");
            password.requestFocus();
            return signupFlag;
        }

        if(!passwordText.matches(upperCaseChars)){
            password.setError("Password must have at least one uppercase character");
            password.requestFocus();
            return signupFlag;
        }

        if(!passwordText.matches(lowerCaseChars)){
            password.setError("Password must have at least one lowercase character");
            password.requestFocus();
            return signupFlag;
        }

        if(!passwordText.matches(numbers)){
            password.setError("Password must have at least one number");
            password.requestFocus();
            return signupFlag;
        }

        if(!passwordText.matches(specialChars)){
            password.setError("Password must have at least one special symbol");
            password.requestFocus();
            return signupFlag;
        }

        if(!passwordText.equals(confirmPasswordText)){
            confirmPassword.setError("The confirm password needs to match the password");
            confirmPassword.requestFocus();
            return signupFlag;
        }

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(usernameText, emailText);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        // Sending verification email
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(getActivity(), "Check your email to verify your account", Toast.LENGTH_LONG).show();
                                        SignupTabFragment.signupFlag = true;

                                        RequestQueue queue = Volley.newRequestQueue(getContext());
                                        queue.start();

                                        StringRequest stringRequest = new StringRequest(Request.Method.GET, HTTPHelpers.MYURL,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        // Add response key to firebase
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child("userkey").setValue(response);
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e("Volley Error", error.toString());
                                                Toast.makeText(getActivity(), "Could not get user key!", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        queue.add(stringRequest);


                                    }
                                    else{
                                        Toast.makeText(getActivity(), "User sign up failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getActivity(), "Failed to signup", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        localSettings.setField("name", usernameText);
        localSettings.setQuestionnaireComplete("0");

        SharedPreferences.Editor editor = getActivity().getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(LocalSettings.DEFAULT, localSettings.getName());
        editor.putString(LocalSettings.DEFAULT, localSettings.getQuestionnaireComplete());
        editor.apply();

        return signupFlag;
    }

}
