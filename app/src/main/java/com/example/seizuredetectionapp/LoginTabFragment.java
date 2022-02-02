package com.example.seizuredetectionapp;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginTabFragment extends Fragment implements View.OnClickListener {

    Button login;
    TextView forgetPassword;
    EditText email, password;
    private FirebaseAuth mAuth;
    float v = 0;
    static boolean loginFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tap_fragment, container, false);

        // Initializing the firebase api token
        mAuth = FirebaseAuth.getInstance();

        // Getting the views by ID
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        forgetPassword = root.findViewById(R.id.forgetPassword);
        login = root.findViewById(R.id.login);

        // Adding click listeners to the login and forget password
        login.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        // Adding animations to the views
        email.setTranslationY(800);
        password.setTranslationY(800);
        forgetPassword.setTranslationY(800);
        login.setTranslationY(800);

        email.setAlpha(v);
        password.setAlpha(v);
        forgetPassword.setAlpha(v);
        login.setAlpha(v);

        email.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgetPassword.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

        return root;
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login:
                // Change to the Datatable page
                boolean flag = loginUser();
                if(flag) {
                    startActivity(new Intent(this.getContext(), Datatable.class));
                }
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(this.getContext(), ForgetPassword.class));
                break;
        }
    }

    // Checking the user inputs and signing them up if valid
    public boolean loginUser(){
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if(emailText.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return loginFlag;
        }

        if(passwordText.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return loginFlag;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Please provide a valid email address");
            email.requestFocus();
            return loginFlag;
        }

        mAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Checking if the user has verified their email
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                    if(user.isEmailVerified()){
                        LoginTabFragment.loginFlag = true;

                    }
                    else{
                        user.sendEmailVerification();
                        Toast.makeText(getActivity(), "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(getActivity(), "Failed to login! Please check your credentials.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return loginFlag;
    }


}
