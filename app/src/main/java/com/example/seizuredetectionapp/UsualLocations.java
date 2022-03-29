package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class UsualLocations extends AppCompatActivity implements View.OnClickListener{

    private SwipeMenuListView swipeMenuListView;
    private Button saveLocationButton, addLocationButton;

    private ArrayList<UsualLocationsLayout> locations = new ArrayList<>();
    private UsualLocationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usual_locations);

        // Initializing the views
        swipeMenuListView = findViewById(R.id.locationListView);
        saveLocationButton = findViewById(R.id.saveLocationButton);
        addLocationButton = findViewById(R.id.addNewLocationButton);

        // Adding the on click listeners to buttons
        saveLocationButton.setOnClickListener(this);
        addLocationButton.setOnClickListener(this);

        // Connecting adaoter to listview
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
                    // Delete contact
                    // adapter.notifyDataSetChanged();
                    break;
            }
            // false : close the menu; true : not close the menu
            return true;
        });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.saveLocationButton:
                break;
            case R.id.addNewLocationButton:
                break;
        }
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, metrics);
    }
}