package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class Navbar extends AppCompatActivity implements View.OnClickListener {

    private static BottomNavigationView bottomNavigationView;
    private LinearLayout linearLayout;
    private SwipeListener swipeListener;
    private String fragmentTag = "datatable";
    FloatingActionButton addJournal;
    private boolean seizureDetected = false;
    private boolean gotoAlert = false;
    private SharedPreferences sharedPreferences;
    private LocalSettings localSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        try{
            seizureDetected = getIntent().getExtras().getBoolean("seizure");
        } catch (Throwable e){
            e.printStackTrace();
        }
        Log.d("seizure", ""+seizureDetected);

        try{
            gotoAlert = getIntent().getExtras().getBoolean("go to alert");
        } catch (Throwable e){
            e.printStackTrace();
        }
        Log.d("go to alert", ""+gotoAlert);

        // Initializing the views
        bottomNavigationView = findViewById(R.id.bottomNavbar);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        linearLayout = findViewById(R.id.linearLayout);
        addJournal = findViewById(R.id.fab);
        addJournal.setOnClickListener(this);

        // Setting the background of the bottomNavigationView to null
        // This prevents conflicts with the bottom app bar view
        bottomNavigationView.setBackground(null);

        // Change to the datatable fragment on create
        // Without this, it goes to alert page
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.datatableFragment); // change to whichever id should be default
        }

        if(seizureDetected || gotoAlert){
            Log.d("made it here", ""+seizureDetected);
            bottomNavigationView.setSelectedItemId(R.id.alertPageFragment);
        }

        // Initialize swipe listener
        swipeListener = new SwipeListener(linearLayout);

    }

    private void runFadeAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.slideinbottom);
        a.reset();
        LinearLayout ll = findViewById(R.id.linearLayout);
        ll.clearAnimation();
        ll.startAnimation(a);
    }

    public static BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    // Navbar controller for forwarding the user to the right fragment
    private BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                /* TODO: stop the countdown timer when the user moves from alert page to another fragment
                    to not save a journal to firebase */

                // Fragment tag is as an identifier for the fragments
                // They help us better identify the fragments during swiping
                switch (item.getItemId()) {
                    case R.id.datatableFragment:
                        selectedFragment = new DatatableFragment();
                        fragmentTag = "datatable";
                        break;
                    case R.id.alertPageFragment:
                        selectedFragment = new AlertPageFragment();
                        fragmentTag = "alertpage";
                        break;
                    case R.id.realtimeFragment:
                        selectedFragment = new RealtimeFragment();
                        fragmentTag = "realtime";
                        break;
                    case R.id.mainSettingsFragment:
                        selectedFragment = new MainSettingsFragment();
                        fragmentTag = "mainsettings";
                        break;
                }

                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2,
                            selectedFragment, fragmentTag).commit();

                }

                return true;
            };

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case(R.id.fab):
                intent = new Intent(Navbar.this, AddJournal.class);
                startActivity(intent);
                break;
        }
    }

    // Swipe listener class which handles swiping based on user touch
    private class SwipeListener implements View.OnTouchListener{

        GestureDetector gestureDetector;
        SwipeListener(View view){
            int threshold = 100;
            int velocityThreshold = 100;

            // Initialize simple gesture listener
            GestureDetector.SimpleOnGestureListener listener =
                    new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onDown(MotionEvent e){
                            return true;
                        }

                        @Override
                        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                            // Get x and y difference
                            float xDiff = e2.getX() - e1.getX();
                            float yDiff = e2.getY() - e1.getY();
                            try{
                                // check condition
                                if(Math.abs(xDiff) > Math.abs(yDiff)){
                                    // When x is greater than y
                                    if(Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocityThreshold){

                                        // Get the current fragment
                                        Fragment nextFragment = null;
                                        Fragment selectedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

                                        if(xDiff < 0){
                                            // When swipe left
                                            if (selectedFragment != null && selectedFragment.isVisible()) {

                                                if(selectedFragment instanceof AlertPageFragment){
                                                    // Stop the countdown timer before moving to other fragments
                                                    // This prevents help request protocol from being invoked
                                                    ((AlertPageFragment) selectedFragment).stopCountDownTimer();
                                                    nextFragment = new DatatableFragment();

                                                    // Update the navbar to match the selected fragment
                                                    bottomNavigationView.setSelectedItemId(R.id.datatableFragment);
                                                    fragmentTag = "datatable";
                                                }
                                                if(selectedFragment instanceof DatatableFragment){
                                                    nextFragment = new MainSettingsFragment();
                                                    bottomNavigationView.setSelectedItemId(R.id.mainSettingsFragment);
                                                    fragmentTag = "mainsettings";
                                                }
                                                if(selectedFragment instanceof MainSettingsFragment){
                                                    nextFragment = new RealtimeFragment();
                                                    bottomNavigationView.setSelectedItemId(R.id.realtimeFragment);
                                                    fragmentTag = "realtime";
                                                }
                                                if(selectedFragment instanceof RealtimeFragment){
                                                    Log.d("Swipe Action", "Real time page has no page to the right of it!");
                                                }
                                            }
                                        }
                                        else{
                                            // when swipe right
                                            if (selectedFragment != null && selectedFragment.isVisible()) {

                                                if(selectedFragment instanceof AlertPageFragment){
                                                    Log.d("Swipe Action", "Alert page has no page to the left of it!");
                                                }
                                                if(selectedFragment instanceof DatatableFragment){
                                                    nextFragment = new AlertPageFragment();
                                                    bottomNavigationView.setSelectedItemId(R.id.alertPageFragment);
                                                    fragmentTag = "alertpage";
                                                }
                                                if(selectedFragment instanceof MainSettingsFragment){
                                                    nextFragment = new DatatableFragment();
                                                    bottomNavigationView.setSelectedItemId(R.id.datatableFragment);
                                                    fragmentTag = "datatable";
                                                }
                                                if(selectedFragment instanceof RealtimeFragment){
                                                    nextFragment = new MainSettingsFragment();
                                                    bottomNavigationView.setSelectedItemId(R.id.mainSettingsFragment);
                                                    fragmentTag = "mainsettings";
                                                }
                                            }
                                        }

                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2,
                                                nextFragment, fragmentTag).commit();
                                        return true;
                                    }
                                }
                                else {
                                    if(Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocityThreshold){
                                        // check condition
                                        if(yDiff > 0){
                                            // When swipe down

                                        }
                                        else{
                                            // when swipe up

                                        }
                                        return true;
                                    }
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();;
                            }
                            return false;
                        }
                    };
            // Initialize gesture detector
            gestureDetector = new GestureDetector(listener);
            // Set listener on view
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }


}