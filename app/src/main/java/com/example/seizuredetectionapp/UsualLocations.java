package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UsualLocations extends AppCompatActivity implements View.OnClickListener{

    private SwipeMenuListView swipeMenuListView;
    private Button saveLocationButton, addLocationButton;
    private ImageView hintImage;
    private TextView textBox, titleBox;

    private ArrayList<UsualLocationsLayout> locations = new ArrayList<>();
    private UsualLocationsAdapter adapter;

    private LocalSettings localSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usual_locations);

        // Initializing the views
        swipeMenuListView = findViewById(R.id.locationListView);
        saveLocationButton = findViewById(R.id.saveLocationButton);
        addLocationButton = findViewById(R.id.addNewLocationButton);
        hintImage = findViewById(R.id.hintUsualLocations);

        // Adding the on click listeners to buttons
        saveLocationButton.setOnClickListener(this);
        addLocationButton.setOnClickListener(this);
        hintImage.setOnClickListener(this);

        // Pulling from local settings
        Set<String> savedLocations  = pullFromLocalSettings();

        // Locations selected in the GoogleMaps page
        ArrayList<String> receivedLocations = new ArrayList<>();
        try {
            receivedLocations = getIntent().getExtras().getStringArrayList("locations");
        } catch (Throwable e1){
            e1.printStackTrace();
        }

        if(receivedLocations != null){
            Log.d("usual locations", receivedLocations.toString());
        }

        // Merging the saved locations and received locations
        if(savedLocations != null){
            Set<String> receivedLocationsSet = new HashSet<>(receivedLocations);
            savedLocations.addAll(receivedLocationsSet);
            Log.d("merged locations", savedLocations.toString());
        }

        // Converting to UsualLocationsLayout ArrayList
        for(String location:savedLocations){
            UsualLocationsLayout usualLocationsLayout = new UsualLocationsLayout(location);
            locations.add(usualLocationsLayout);
        }

        // Connecting adapter to listview
        adapter = new UsualLocationsAdapter(this, R.layout.item_usual_location, locations);
        swipeMenuListView.setAdapter(adapter);

        // Slider for listview for deleting locations
        SwipeMenuCreator creator = menu -> {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(dp2px(this, 90));
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        // Adding the delete slider to listview
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setCloseInterpolator(new BounceInterpolator());

        // Checking what gets deleted in the list
        swipeMenuListView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    // Delete location
                    locations.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
            // false : close the menu; true : not close the menu
            return true;
        });
    }

    @Override
    public void onClick(View view){
        // Save their location when save locations and add new location buttons are clicked
        pushToLocalSettings();

        switch(view.getId()){
            case R.id.saveLocationButton:
                startActivity(new Intent(this, Navbar.class));
                break;
            case R.id.addNewLocationButton:
                startActivity(new Intent(this, GoogleMaps.class));
                break;
            case R.id.hintUsualLocations:
                showHint(view.getContext());
                break;
        }
    }

    public Set<String> convertAdapterToSet(){
        Set<String> locationsSet = new HashSet<>();

        for(UsualLocationsLayout locationsLayout:locations){
            String location = locationsLayout.getStreet() + " ";
            locationsSet.add(location);
        }

        return locationsSet;
    }

    public void pushToLocalSettings(){
        // Converting locations array to a set
        Set<String> locationsSet = convertAdapterToSet();

        // Saving the locations list
        localSettings.setLocations(locationsSet);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("locations", localSettings.getLocations());

        // Logging the status of push
        if(editor.commit()){
            Log.d("locations status", "Successful");
        } else{
            Log.d("locations status", "Failed");
        }
    }

    public Set<String> pullFromLocalSettings(){
        Set<String> locations = new HashSet<>();

        SharedPreferences sharedPreferences = getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        locations = sharedPreferences.getStringSet("locations", LocalSettings.getLocations());

        return locations;
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, metrics);
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
        textBox = dialog.getWindow().findViewById(R.id.textView2);
        titleBox = dialog.getWindow().findViewById(R.id.textView);
        textBox.setText("Keeping track of your usual locations helps STRapp notify your emergency contacts. The more specific the location the better!");
        titleBox.setText("Usual Locations");

        Button gotIt = dialog.findViewById(R.id.btn_gotit);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}