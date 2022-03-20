package com.example.seizuredetectionapp;

import static com.example.seizuredetectionapp.Questionnaire.addedContacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.HashSet;
import java.util.Set;

public class UpdateContacts extends AppCompatActivity implements View.OnClickListener{

    private SwipeMenuListView listView;
    private Button changeContactListButton, saveButton;
    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayAdapter adapter;
    private String[] contactValues;
    private LocalSettings localSettings;
    View v;

    RecyclerView recyclerView;
    ArrayList<UpdateContactLayout> contactList = new ArrayList<UpdateContactLayout>();
    //UpdateContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contacts);

        listView = findViewById(R.id.listView);
        //recyclerView = findViewById(R.id.updatecontactRecycler);

        changeContactListButton = findViewById(R.id.changeContactList);
        saveButton = findViewById(R.id.saveButton);

        changeContactListButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // Stores our active xml for later use
        //v = getLayoutInflater().inflate(R.layout.activity_contacts_page, null);
        //setContentView(v);

        // Getting the user contacts
        Set<String> savedContacts = getUserContacts();
        Log.d("pulled contacts", savedContacts.toString());

        // merge the addcontacts and savedcontacts
        if(addedContacts != null){
            Log.d("added contacts", ""+addedContacts.toString());
            savedContacts.addAll(addedContacts);
            Log.d("merged contacts", ""+savedContacts.toString());
        }

        // Convert saved contacts to string array
        contactValues = savedContacts.toArray(new String[savedContacts.size()]);

        // convert string list to array list string
        Collections.addAll(contacts, contactValues);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(adapter);

        //recyclerView = findViewById(R.id.updatecontactRecycler);
        //getContactList(v);

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
            deleteItem.setIcon(R.drawable.ic_delete);
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
                    contacts.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
            // false : close the menu; true : not close the menu
            return true;
        });

    }

    /*
    private void getContactList(View v) {
        for(String contact:contacts){
            // creates a new instance of the class used to generate each contact
            //UpdateContactLayout layout = new UpdateContactLayout();
            layout.setNumber(contact);

            // puts new contact into a list for later use
            contactList.add(layout);
        }
        // uses our template and list of layouts to fill the contacts page
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Set<String> listOfContacts = new HashSet<>();

        //adapter = new UpdateContactAdapter(this, contactList, v, listOfContacts);
        //recyclerView.setAdapter(adapter);
    }

     */

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.changeContactList:
                // TODO: Move to the contact list activity and get user contacts
                saveUpdatedContacts();
                Intent intent = new Intent(this, ContactsPage.class);
                intent.putExtra("settings page", true);
                startActivity(intent);
                break;
            case R.id.saveButton:
                // TODO: Save the user changes to the local settings
                saveUpdatedContacts();
                startActivity(new Intent(UpdateContacts.this, AppSettings.class));
                break;
        }
    }

    private Set<String> getUserContacts() {

        SharedPreferences sharedPreferences = getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        Set<String> contacts = sharedPreferences.getStringSet("contact method", localSettings.getContactList());

        return contacts;
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, metrics);/*from   w ww  .ja  va2s.co  m*/
    }

    public void saveUpdatedContacts(){
        Log.d("new contacts", ""+contacts.toString());
        Set<String> newContacts = new HashSet<>(contacts);
        localSettings.setContactList(newContacts);
        SharedPreferences.Editor editor = getSharedPreferences(localSettings.PREFERENCES, MODE_PRIVATE).edit();
        editor.putStringSet("contact method", localSettings.getContactList());
        if(editor.commit()){
            Log.d("contacts status", "Successful");
        } else{
            Log.d("contacts status", "Failed");
        }
        addedContacts = null;
    }

    @Override
    public void onBackPressed(){

        saveUpdatedContacts();
        finish();
    }

}