package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Navbar extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout linearLayout;
    private SwipeListener swipeListener;
    private String fragmentTag = "datatable";
    private int alertPageId;
    private int datatableId = 2131296885;
    private int mainSettingsId;
    private int realtimeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        bottomNavigationView = findViewById(R.id.bottomNavbar);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        linearLayout = findViewById(R.id.linearLayout);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.datatableFragment); // change to whichever id should be default
        }

        // Initialize swipe listener
        swipeListener = new SwipeListener(linearLayout);

    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

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
                                    // check condition
                                    if(Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocityThreshold){
                                        // check condition
                                        Fragment nextFragment = null;
                                        Fragment selectedFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
                                        if(xDiff > 0){
                                            // When swipe right
                                            if (selectedFragment != null && selectedFragment.isVisible()) {
                                                int fragmentId = selectedFragment.getId();
                                                Log.d("datatable id", ""+String.valueOf(fragmentId));
                                                if(fragmentId == alertPageId){

                                                }
                                                if(fragmentId == datatableId){
                                                    nextFragment = new MainSettingsFragment();
                                                    fragmentTag = "mainsettings";
                                                }
                                                if(fragmentId == mainSettingsId){

                                                }
                                                if(fragmentId == realtimeId){

                                                }
                                            }
                                        }
                                        else{
                                            // when swipe left
                                            if (selectedFragment != null && selectedFragment.isVisible()) {
                                                int fragmentId = selectedFragment.getId();
                                                if(fragmentId == alertPageId){

                                                }
                                                if(fragmentId == datatableId){
                                                    nextFragment = new AlertPageFragment();
                                                    fragmentTag = "alertpage";
                                                }
                                                if(fragmentId == mainSettingsId){

                                                }
                                                if(fragmentId == realtimeId){

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