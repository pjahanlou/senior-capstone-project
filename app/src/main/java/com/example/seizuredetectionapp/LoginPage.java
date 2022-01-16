package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class LoginPage extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    FloatingActionButton facebook, google;
    float opacity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Getting the Views
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        facebook = findViewById(R.id.fab_facebook);
        google = findViewById(R.id.fab_google);

        // Initializing the login adapter for the tabs
        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        // Adding the tab texts to the tab layout
        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign up"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // Adding animations to the social media buttons and tab layout
        facebook.setTranslationY(300);
        google.setTranslationY(300);
        tabLayout.setTranslationY(300);

        facebook.setAlpha(opacity);
        google.setAlpha(opacity);
        tabLayout.setAlpha(opacity);

        facebook.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        Toast.makeText(this, "Firebase Connection Success", Toast.LENGTH_LONG).show();

    }
}