package com.example.seizuredetectionapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LoginAdapter extends FragmentStateAdapter {

    public LoginAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lifecycle){
        super(fm, lifecycle);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    // Create the right tab based on the position
    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position){
            case 1:
                SignupTabFragment signupTabFragment = new SignupTabFragment();
                return signupTabFragment;
        }
        LoginTabFragment loginTabFragment = new LoginTabFragment();
        return loginTabFragment;
    }

}
