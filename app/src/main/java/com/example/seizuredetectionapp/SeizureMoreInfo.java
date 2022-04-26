package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SeizureMoreInfo extends AppCompatActivity implements View.OnClickListener {


    private Button back, tonic, generalizedTonicClonic
            ,clonic
            ,absence
            ,myoclonic
            ,atonic
            ,epilepticSeizures;
    String ID;
    String previousPage;
    Boolean addJournalEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_more_info);

        //get buttons
        Log.d("page - SeizureMoreInfo", "SeizureMoreInfo Before Button back 1");
        back = findViewById(R.id.seizure_back);
        generalizedTonicClonic = findViewById(R.id.Generalized_tonic_clonic);
        tonic = findViewById(R.id.Tonic);
        clonic = findViewById(R.id.Clonic);
        absence = findViewById(R.id.Absence);
        myoclonic = findViewById(R.id.Myoclonic);
        atonic = findViewById(R.id.Atonic);
        epilepticSeizures = findViewById(R.id.Epileptic_Seizure);
        Log.d("page - SeizureMoreInfo", "SeizureMoreInfo Before Button back 2");

        //set listeners
        back.setOnClickListener(this);
        generalizedTonicClonic.setOnClickListener(this);
        tonic.setOnClickListener(this);
        clonic.setOnClickListener(this);
        absence.setOnClickListener(this);
        myoclonic.setOnClickListener(this);
        atonic.setOnClickListener(this);
        epilepticSeizures.setOnClickListener(this);

        try{
            ID = getIntent().getExtras().getString("id");
            previousPage = getIntent().getExtras().getString("page");
            addJournalEdit = getIntent().getExtras().getBoolean("key");
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d("DID KEY GETTOMOREINFO",ID);
    }
    private void showHint(Context context, String type, String explanation) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.seizure_type_info_template);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button dialogBack = dialog.findViewById(R.id.btn_back);

        dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.Generalized_tonic_clonic:
                showHint(this, "Generalized Tonic-Clonic","");
                break;
            case R.id.Tonic:
                showHint(this, "Tonic","");
                break;
            case R.id.Clonic:
                showHint(this, "Clonic","");
                break;
            case R.id.Absence:
                showHint(this, "Absence","");
                break;
            case R.id.Myoclonic:
                showHint(this, "Myoclonic","");
                break;
            case R.id.Atonic:
                showHint(this, "Atonic","");
                break;
            case R.id.Epileptic_Seizure:
                showHint(this, "Epileptic Seizure","");
                break;
            case R.id.seizure_back:
                if(previousPage.equals("AddJournal")){
                    Intent intent = new Intent(this, AddJournal.class);
                    Log.d("page - AddJournal", "Previous Page was AddJournal");
                    intent.putExtra("id",ID);
                    intent.putExtra("key",true);
                    startActivity(intent);
                } else {
                    break;
                }
                break;
        }

    }
}