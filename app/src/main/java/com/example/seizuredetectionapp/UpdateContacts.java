package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import cucumber.api.java.cs.A;

public class UpdateContacts extends AppCompatActivity implements View.OnClickListener{

    private SwipeMenuListView listView;
    private Button changeContactListButton, saveButton;
    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayAdapter adapter;
    private String[] contactValues;
    private LocalSettings localSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);

        listView = findViewById(R.id.listView);
        changeContactListButton = findViewById(R.id.changeContactList);
        saveButton = findViewById(R.id.saveButton);

        changeContactListButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        getUserContacts();
        Log.d("User contacts list", ""+contactValues.toString());
        Collections.addAll(contacts, contactValues);
        Log.d("contacts array", ""+contacts.toString());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
                deleteItem.setWidth(dp2px(UpdateContacts.this, 90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        listView.setMenuCreator(creator);
        listView.setCloseInterpolator(new BounceInterpolator());

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // Delete contact
                        contacts.remove(position);
                        adapter.notifyDataSetChanged();
                        break;
                }
                // false : close the menu; true : not close the menu
                return true;
            }
        });

    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.changeContactList:
                // TODO: Move to the contact list activity and get user contacts
                break;
            case R.id.saveButton:
                // TODO: Save the user changes to the local settings
                break;
        }
    }

    private void getUserContacts() {

        SharedPreferences sharedPreferences = getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        Set<String> contacts = sharedPreferences.getStringSet("saved contact list", localSettings.getContactList());

        contactValues = contacts.toArray(new String[contacts.size()]);
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, metrics);/*from   w ww  .ja  va2s.co  m*/
    }

}