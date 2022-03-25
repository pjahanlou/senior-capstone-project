package com.example.seizuredetectionapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.onlynight.waveview.WaveView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainSettingsFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CircleImageView userPicture;
    private TextView usernameText, emailText;
    private Button profileButton, appButton, logoutButton;

    private FirebaseUser currentUser;
    private String currentUserUID;
    private String username, email;

    private Uri imageUri;
    File localFile = null;
    private WaveView waveView;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> someActivityResultLauncher;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainSettingsFragment newInstance(String param1, String param2) {
        MainSettingsFragment fragment = new MainSettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Getting user info
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserUID = currentUser.getUid();
        email = currentUser.getEmail();

        // initializing firebase cloud storage
        storageReference = FirebaseStorage.getInstance().getReference("users/"+currentUserUID+".jpg");

        // Setting the user picture at the beginning
        try {
            Log.d("tag", "made it here");
            localFile = File.createTempFile("users", "jpg", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 16;
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath(), opts);
            userPicture.setImageBitmap(bitmap);
            Toast.makeText(getContext(), "Picture Uploaded Successfully.", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "No Picture Found.", Toast.LENGTH_LONG).show();
            }
        });

        // Retrieving the user name and updating the main page
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LocalSettings.getField("name"), Context.MODE_PRIVATE);
        username = sharedPreferences.getString(LocalSettings.DEFAULT, LocalSettings.name);

        // Handling picture uploading in the main settings
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        // Change the image view to user image
                        imageUri = data.getData();
                        userPicture.setImageURI(imageUri);

                        // Uploading user image to firebase cloud storage
                        storageReference.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    Toast.makeText(getContext(), "Picture uploaded successfully!", Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Picture upload failed!", Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_settings, container, false);

        // Initializing the views
        userPicture = root.findViewById(R.id.profileImage);
        usernameText = root.findViewById(R.id.usernameText);
        emailText = root.findViewById(R.id.emailText);
        profileButton = root.findViewById(R.id.profileSettingsButton);
        appButton = root.findViewById(R.id.appSettingsButton);
        logoutButton = root.findViewById(R.id.logoutButton);
        waveView = root.findViewById(R.id.imageView3);
        waveView.start();

        // Adding click listeners to the buttons and imageview
        userPicture.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        appButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        // Retrieving the username and email
        setUserInfo();

        return root;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.profileImage:
                selectImage();
                break;
            case R.id.profileSettingsButton:
                startActivity(new Intent(getContext(), ProfileSettings.class));
                break;
            case R.id.appSettingsButton:
                startActivity(new Intent(getContext(), AppSettings.class));
                break;
            case R.id.logoutButton:
                logoutUser();
                break;
        }
    }

    /**
     * Method for handling opening the user gallery
     *
     * */
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);

    }

    /**
     * Method for setting the username and email in UI
     */
    private void setUserInfo(){
        if (username != null){
            usernameText.setText("Hello " + username + "!");
        }
        else {
            usernameText.setText("Hello User!");
        }
        emailText.setText(email);
    }

    /**
     * Method for logging out the user
     */
    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), LoginPage.class));
    }
}