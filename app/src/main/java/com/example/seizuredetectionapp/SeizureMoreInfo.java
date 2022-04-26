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
import android.widget.TextView;

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
        TextView titleBox = dialog.findViewById(R.id.titleText);
        TextView mainBox = dialog.findViewById(R.id.mainText);
        titleBox.setText(type);
        mainBox.setText(explanation);
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
            // Sources for seizure information: https://www.hopkinsmedicine.org/health/conditions-and-diseases/epilepsy/generalized-seizures#:~:text=Atonic%20Seizures%20(Drop%20Attacks),or%20collapse%2C%20possibly%20causing%20injury.
            case R.id.Generalized_tonic_clonic:
                showHint(this, "Generalized Tonic-Clonic","Many people with generalized tonic-clonic seizures have vision, " +
                        "taste, smell, or sensory changes before the seizure. This is called aura. This is followed by violent muscle contractions " +
                        "and loss of alertness");
                break;
            case R.id.Tonic:
                showHint(this, "Tonic","Tonic seizures causes a sudden stiffness or tension in th muscles or the arms, legs or torso. " +
                        "The stiffness last about 20 seconds and is most likely to happen during sleep. Tonic seizures that occur while the person is " +
                        "standing may cause them to fall");
                break;
            case R.id.Clonic:
                showHint(this, "Clonic","Clonic seizures are characterized by repeated jerking movements of the arms and legs on one or both sides of the body, " +
                        "sometimes with numbness or tingling. If it is a partial seizure, the person may be aware of what's happening.");
                break;
            case R.id.Absence:
                showHint(this, "Absence","Absence seizures are more common among children than adults. Common symptoms are: being very still, smacking the lips or " +
                        "making a chewing motion with the mouth, fluttering the eyelids, suddenly not talking or moving, and small movements of both hands.");
                break;
            case R.id.Myoclonic:
                showHint(this, "Myoclonic","Myoclonic seizures are characterized by brief, jerking spasms of a muscle or muscle group. They can often " +
                        "occur with atonic seizures. Each individual seizure lasts about 1-3 seconds but can happen in clusters");
                break;
            case R.id.Atonic:
                showHint(this, "Atonic","Atonic seizure, also known as drop attacks, involves a sudden decrease in muscle tone, causing a person's body to go limp, slump, or collapse. ");
                break;
            case R.id.Epileptic_Seizure:
                showHint(this, "Infantile Spasms","Infantile spasms typically begin between 3 and 12 months of age and suddenly stop by the age of 2 to 4 years. " +
                        "The spasms appear as a sudden jerk or jolt followed by stiffening. Often the child's arms fling outward and the knees pull up as the body bends forward.");
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