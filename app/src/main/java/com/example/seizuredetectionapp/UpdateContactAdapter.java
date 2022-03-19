package com.example.seizuredetectionapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UpdateContactAdapter extends RecyclerView.Adapter<ContactsAdapter.UserViewHolder> {

    Set<String> listOfContacts = new HashSet<>();
    View v;
    Activity activity;
    ArrayList<UpdateContactLayout> contactList;

    public UpdateContactAdapter(Activity activity, ArrayList<UpdateContactLayout> contactList, View v, Set<String> listOfContacts){
        this.activity = activity;
        this.contactList = contactList;
        this.v = v;
        this.listOfContacts = listOfContacts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactsAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.UserViewHolder holder, int position) {
        UpdateContactLayout model = contactList.get(position);

        holder.phoneNumber.setText((model.getNumber()));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView phoneNumber;
        //Set<String> listOfContacts = new HashSet<>();

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            phoneNumber = itemView.findViewById(R.id.contactEditViewNumber);
            itemView.findViewById(R.id.addContact_Button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String textPhoneNumber = phoneNumber.getText().toString().trim();
                            Log.d("demo", "button Clicked on contact: " + textPhoneNumber);
                            listOfContacts.add(textPhoneNumber);
                            Log.d("my check", "" + listOfContacts.toString());
                        }
                    });
        }
    }
}
