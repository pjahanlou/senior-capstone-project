package com.example.seizuredetectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsPage extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ContactLayout> contactList = new ArrayList<ContactLayout>();
    MainAdapter adapter;
    View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        // Stores our active xml for later use
        View v = getLayoutInflater().inflate(R.layout.activity_contacts_page, null);
        setContentView(v);

        // stores the view for our template contact
        recyclerView = findViewById(R.id.contactRecycler);
        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecycleritemClickListener.OnItemClickListener(){

                })
        );*/
        checkPermission(v);
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

        ImageButton btn = (ImageButton) findViewById(R.id.addContact_Button);
        adapter = new MainAdapter(ContactsPage.this, contactList, v, btn);
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