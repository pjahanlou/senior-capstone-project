package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener {

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 69;
    private TextView changeEmailText, changeDisplayNameText;
    private Button updateEmailButton, updateDisplayNameButton, changePasswordButton, deleteAccountButton, exportDataButton;
    private ImageView hintImage;

    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String currentUserUID;

    ArrayList<Journal> savedJournals = new ArrayList<>();
    ArrayList<String> listOfJournals = new ArrayList<>();

    int pdfWidth = 1080;
    int pdfHeight = 1920;

    private String text;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // initializing firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(currentUserUID);

        // Initializing the views
        changeEmailText = findViewById(R.id.changeEmailText);
        changeDisplayNameText = findViewById(R.id.changeDisplayNameText);
        updateEmailButton = findViewById(R.id.submitNewEmail);
        updateDisplayNameButton = findViewById(R.id.submitNewDisplayName);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        exportDataButton = findViewById(R.id.exportDataButton);
        hintImage = findViewById(R.id.hintProfileSettings);

        // Setting the on click listeners
        updateEmailButton.setOnClickListener(this);
        updateDisplayNameButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);
        exportDataButton.setOnClickListener(this);
        hintImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submitNewEmail:
                updateEmail();
                break;
            case R.id.submitNewDisplayName:
                updateDisplayName();
                break;
            case R.id.changePasswordButton:
                startActivity(new Intent(ProfileSettings.this, ForgetPassword.class));
                break;
            case R.id.deleteAccountButton:
                deleteAccount();
                startActivity(new Intent(ProfileSettings.this, LoginPage.class));
                break;
            case R.id.exportDataButton:
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_PERMISSION_CODE);
                break;
            case R.id.hintProfileSettings:
                showHint(view.getContext());
                break;

        }
    }



    /**
     * Method for handling display name updates
     *
     * */
    private void updateDisplayName(){
        String TAG = "New Display Name";
        String newDisplayName = changeDisplayNameText.getText().toString().trim();

        if(newDisplayName.isEmpty()){
            changeDisplayNameText.setError("Display name is required!");
            changeDisplayNameText.requestFocus();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build();

        // Update the user profile in Firebase Authentication
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Display name updated.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User profile updated.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Display name update failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });

    }

    /**
     * Method for handling email update
     * */
    private void updateEmail(){
        String TAG = "New Email Address";
        String newEmail = changeEmailText.getText().toString().trim();

        // Validating user input
        if(newEmail.isEmpty()){
            changeEmailText.setError("Email is required!");
            changeEmailText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
            changeEmailText.setError("Please provide a valid email address");
            changeEmailText.requestFocus();
            return;
        }

        // Updating their email in Firebase Authentication
        currentUser.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Email updated.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User email address updated.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Email update failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });

    }

    /**
     * Method for handling user deleting their account
     * */
    private void deleteAccount(){
        String TAG = "Delete Account";

        currentUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSettings.this, "Account deleted.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User account deleted.");
                    }
                    else{
                        Toast.makeText(ProfileSettings.this, "Delete account failed!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, task.getException().toString());
                    }
                });
    }

    /**
     * Method for Creating and Saving Pdf to Local Directory
     */
    private void createPdf() throws IOException {
        //create pdf document
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ArrayList<Journal> listOfJournals = new ArrayList<>();
                Journal journalToPdf = snapshot.getValue(Journal.class);
                listOfJournals.add(journalToPdf);
                PdfDocument document = new PdfDocument();

                Paint paint = new Paint();
                Paint title = new Paint();

                //set pdf height and width
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pdfWidth, pdfHeight, 1).create();

                //start pdf page
                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas canvas = page.getCanvas();

                title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                //set size of text
                title.setTextSize(20);

                canvas.drawText("Logged Journals", 100, 200, title);
                int x = 10;
                int y = 25;
                //Iterates through each saved journal in firebase and draws each entry under each other
                for (Journal journal : listOfJournals) {
                    page.getCanvas().drawText(String.valueOf(journal), x, y, paint);
                    y += paint.descent() - paint.ascent();
                }

                //close pdf page
                document.finishPage(page);

                //downloads directory
                File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)), "Journals.pdf");

                try {
                    //save file to downloads directory
                    document.writeTo(new FileOutputStream(file));
                    Toast.makeText(ProfileSettings.this, "PDF Saved.", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    Toast.makeText(ProfileSettings.this, "PDF Upload Failed.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
                document.close();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     * Method for accepting location permission
     * */
    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileToExternalStorage("Journals","Journals");
                Toast.makeText(this, "Pdf Saved.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "External Storage Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(this, "External Storage Permission Denied", Toast.LENGTH_SHORT) .show();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileToExternalStorage("Journals","Journals");
                Toast.makeText(this, "Pdf Saved.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private void saveFileToExternalStorage(String displayName, String content) {
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Journal journalToPdf = snapshot.getValue(Journal.class);
                Log.d("WOWOEOESE222222", journalToPdf.toString());
                listOfJournals.add(journalToPdf.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Uri externalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String relativeLocation = Environment.DIRECTORY_DOCUMENTS;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        LocalDate localDate = LocalDate.now();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, displayName + "_"+dtf.format(localDate)+".txt");
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/text");
        contentValues.put(MediaStore.Files.FileColumns.TITLE, "Journals");
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativeLocation);
        contentValues.put(MediaStore.Files.FileColumns.DATE_TAKEN, System.currentTimeMillis());

        text = "";
        Log.d("WOEWOEOOEOEOEOWEOE", listOfJournals.toString());
        for (String journal: listOfJournals) {
            text += "\n" + journal;
            Log.d("HERE", text);
        }

        Uri fileUri = getContentResolver().insert(externalUri, contentValues);
        try {
            OutputStream outputStream =  getContentResolver().openOutputStream(fileUri);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showHint(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_settings_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button gotIt = dialog.findViewById(R.id.btn_gotit);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private ArrayList<String> getContent(){

        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Journal journalToPdf = snapshot.getValue(Journal.class);
                Log.d("WOWOEOESE222222", journalToPdf.toString());
                listOfJournals.add(journalToPdf.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listOfJournals;
    }

}