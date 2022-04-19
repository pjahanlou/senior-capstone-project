package com.example.seizuredetectionapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
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
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UpdateContacts extends AppCompatActivity implements View.OnClickListener{

    private SwipeMenuListView listView;
    private Button changeContactListButton, saveButton;
    private ArrayList<UpdateContactLayout> contacts = new ArrayList<>();
    private ArrayAdapter adapter;
    private String[] contactValues;
    private LocalSettings localSettings;
    ArrayList<UpdateContactLayout> contactList = new ArrayList<UpdateContactLayout>();
    private String wasAlertPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);

        // Initializing the views
        listView = findViewById(R.id.listView);
        changeContactListButton = findViewById(R.id.changeContactList);
        saveButton = findViewById(R.id.saveButton);

        changeContactListButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        try{
            wasAlertPage = getIntent().getExtras().getString("page");
            Log.d("Alert page prev", ""+wasAlertPage);
        } catch (Throwable e){
            e.printStackTrace();
        }

        // Getting the user contacts hashmap
        Map<String, String> contactMapSave = loadContactMap();
        Log.d("contact map", ""+contactMapSave.toString());

        // Merging contactMap from ContactsPage and the previous Contacts
        /*
        if(contactMap != null){
            contactMapSave.putAll(contactMap);
            Log.d("merged contacts", ""+contactMapSave.toString());
        }

         */

        // Iterating through the merged contact map and adding them to the listview
        Iterator hmIterator = contactMapSave.entrySet().iterator();
        while(hmIterator.hasNext()){
            Map.Entry contact = (Map.Entry)hmIterator.next();
            String name = (String) contact.getValue();
            String number = (String) contact.getKey();
            UpdateContactLayout updateContactLayout = new UpdateContactLayout(name, number);
            contacts.add(updateContactLayout);
        }
        adapter = new UpdateContactAdapter(this, R.layout.item_update_contact, contacts);
        listView.setAdapter(adapter);

        // Swipe up for deleting contacts
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
            deleteItem.setWidth(dp2px(UpdateContacts.this, 90));
            // set a icon
            //deleteItem.setIcon(getDrawable(R.drawable.ic_delete));
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        // set creator
        listView.setMenuCreator(creator);
        listView.setCloseInterpolator(new BounceInterpolator());

        listView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    // Delete contact
                    Log.d("position to delete", ""+position);
                    Log.d("contact at position", contacts.get(position).toString());
                    Log.d("contact layout list", ""+contacts.toString());
                    contacts.remove(position);
                    Log.d("contact layout list", ""+contacts.toString());
                    //adapter = new UpdateContactAdapter(this, R.layout.item_update_contact, contacts);
                    adapter.remove(position);
                    listView.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    break;
            }
            // false : close the menu; true : not close the menu
            return true;
        });

    }

    /**
     * Method for loading the contact hashmap from shared preferences
     * */
    private Map<String, String> loadContactMap() {
        Map<String, String> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("contact map", (new JSONObject()).toString());
                if (jsonString != null) {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Iterator<String> keysItr = jsonObject.keys();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        String value = jsonObject.getString(key);
                        outputMap.put(key, value);
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return outputMap;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.changeContactList:
                // TODO: Move to the contact list activity and get user contacts
                saveContactMap();
                Intent intent = new Intent(this, ContactsPage.class);
                intent.putExtra("settings page", true);
                startActivity(intent);
                break;
            case R.id.saveButton:
                // TODO: Save the user changes to the local settings
                saveContactMap();
                navigateToNextPage();
                break;
        }
    }

    private void navigateToNextPage(){
        if(wasAlertPage != null){
            if(wasAlertPage.equals("alert page")){
                Intent intent = new Intent(this, Navbar.class);
                intent.putExtra("go to alert", true);
                startActivity(intent);
            }
        } else{
            startActivity(new Intent(this, AppSettings.class));
        }
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, metrics);
    }

    public void saveUpdatedContacts(){
        Log.d("new contacts", ""+contacts.toString());
        Set<String> newContacts = new HashSet<>();
        for(UpdateContactLayout contactLayout:contacts){
            newContacts.add(contactLayout.getNumber());
        }
        localSettings.setContactList(newContacts);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("contact method", localSettings.getContactList());
        if(editor.commit()){
            Log.d("contacts status", "Successful");
        } else{
            Log.d("contacts status", "Failed");
        }
        //addedContacts = null;
    }

    /**
     * Method for saving the contact hashmap to shared preferences
     * */
    private void saveContactMap() {
        Map<String, String> newContacts = new HashMap<>();
        for(UpdateContactLayout contactLayout:contacts){
            newContacts.put(contactLayout.getNumber(), contactLayout.getName());
        }
        Log.d("saveContactsMap", newContacts.toString());

        SharedPreferences pSharedPref = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(newContacts);
            String jsonString = jsonObject.toString();
            pSharedPref.edit()
                    .remove("contact map")
                    .putString("contact map", jsonString)
                    .apply();
        }
        //addedContacts = null;
        //contactMap = null;
    }

    @Override
    public void onBackPressed(){

        saveContactMap();
        finish();
    }

}