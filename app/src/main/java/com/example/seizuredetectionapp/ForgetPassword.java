package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Preventing the user screen from being configured when the keyboard slides up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initializing Firebase token
        mAuth = FirebaseAuth.getInstance();

        // Initializing the views
        email = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.resetPassword);
        resetPasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.resetPassword:
                resetPassword();
                break;
        }
    }

    public void resetPassword(){
        String emailText = email.getText().toString().trim();

        // Validating the user input
        if(emailText.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Please provide a valid email address");
            email.requestFocus();
            return;
        }

        // Sending the user a forget password email regarding their password change
        mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ForgetPassword.this, "Try again! Something wrong happened", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}