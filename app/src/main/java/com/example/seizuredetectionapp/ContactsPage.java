package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;
import static com.example.seizuredetectionapp.Questionnaire.contactMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;


public class ContactsPage extends AppCompatActivity implements Serializable {
    RecyclerView recyclerView;
    ArrayList<ContactLayout> contactList = new ArrayList<ContactLayout>();
    Map<String, String> contactMapSave = new HashMap<>();
    ContactsAdapter adapter;
    View v;
    Button done;
    private boolean settings;
    private SearchView searchView;
    private LocalSettings localSettings;
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);

        // Checking to see if the previous activity was AppSettings
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            settings = extras.getBoolean("settings page");
            Log.d("settings", ""+settings);
        }

        try{
            page = extras.getString("page");
        } catch(Throwable e){
            e.printStackTrace();
        }

        // Stores our active xml for later use
        View v = getLayoutInflater().inflate(R.layout.activity_contacts_page, null);
        setContentView(v);

        // stores the view for our template contact
        recyclerView = findViewById(R.id.contactRecycler);
        checkPermission(v);

        // SearchView Initializing
        searchView = findViewById(R.id.searchButton);
        searchView.clearFocus();

        // Initializing done button
        done = findViewById(R.id.done_Button);
        done.setOnClickListener(this::onClick);

        // SearchView Behavior
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Filtering the contact list every time user types
                filterList(s);
                return false;
            }
        });


    }

    /**
     * Method for filtering through the contact list based on user input
     * If no users are found, a toast gets displayed
     * */
    private void filterList(String s) {
        // Getting each of the contact layout for displaying
        ArrayList<ContactLayout> filteredList = new ArrayList<>();
        for(ContactLayout contact: contactList){
            if(contact.getName().toLowerCase().contains(s.toLowerCase())){
                filteredList.add(contact);
            }
        }

        // If filtered list empty then show toast
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No Contact found", Toast.LENGTH_LONG).show();
        } else{
            // Updating the adapter
            adapter.setFilteredList(filteredList);
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            // returns the user to the previous page with the selected contacts
            case R.id.done_Button: {
                // Checking to see which page is asking for the added contacts
                Map<String, String> savedContacts = loadContactMap();

                if(!savedContacts.isEmpty()){
                    savedContacts.putAll(adapter.contactMap);
                    Log.d("contactMap in Contacts", savedContacts.toString());
                    saveContactMap(savedContacts);
                } else{
                    saveContactMap(adapter.contactMap);
                }

                // Saving the contact hashmap to local settings
                Log.d("finished contacts", "button Clicked on contact: " + adapter.listOfContacts);
                if(settings){
                    Intent intent = new Intent(this, UpdateContacts.class);
                    if(page != null){
                        intent.putExtra("page", page);
                    }
                    startActivity(intent);
                }
                finish();
            }
        }
    }

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

    /**
     * Method for saving the contact hashmap to shared preferences
     * */
    private void saveContactMap(Map<String, String> inputMap) {
        SharedPreferences pSharedPref = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            pSharedPref.edit()
                    .remove("contact map")
                    .putString("contact map", jsonString)
                    .apply();
        }
    }

    private void checkPermission(View v) {
        // if the app does not have permission to access the devices contacts, then request access
        if(ContextCompat.checkSelfPermission(ContactsPage.this, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactsPage.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        else{
            getContactList(v);
        }
    }

    private void getContactList(View v) {
        //tools used to navigate through the contacts and retrieve the contact info one by one
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, null, null, null, sort);

        // while the cursor still has contacts to go through move to next contact
        if (cursor.getCount()>0){
            while(cursor.moveToNext()){

                //stores contact name and id from device contacts
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                Cursor phoneCursor = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if(phoneCursor.moveToNext()){
                    // stores phone number
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // creates a new instance of the class used to generate each contact
                    ContactLayout layout = new ContactLayout();
                    layout.setName(name);
                    layout.setPhoneNumber(number);

                    // puts new contact into a list for later use
                    contactList.add(layout);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        // uses our template and list of layouts to fill the contacts page
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Set<String> listOfContacts = new HashSet<>();

        adapter = new ContactsAdapter(ContactsPage.this, contactList, v, listOfContacts);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((requestCode == 100) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            getContactList(v);
        }
        else {
        Toast.makeText(ContactsPage.this, "Permission Denied.", Toast.LENGTH_LONG).show();
        checkPermission(v);
        }
    }
}