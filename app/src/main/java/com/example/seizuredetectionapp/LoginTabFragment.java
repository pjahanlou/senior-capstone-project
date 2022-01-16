package com.example.seizuredetectionapp;

import android.content.Intent;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

public class LoginTabFragment extends Fragment implements View.OnClickListener {

    Button login;
    TextView forgetPassword;
    EditText email, password;
    float v = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tap_fragment, container, false);

        // Getting the views by ID
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        forgetPassword = root.findViewById(R.id.forgetPassword);
        login = root.findViewById(R.id.login);
        login.setOnClickListener(this);

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
                startActivity(new Intent(this.getContext(), Datatable.class));
                break;
        }
    }
}
