package com.example.seizuredetectionapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.UserViewHolder>{
    static Set<String> listOfContacts = new HashSet<>();
    View v;
    Activity activity;
    ArrayList<ContactLayout> contactList;

    public ContactsAdapter(Activity activity, ArrayList<ContactLayout> contactList, View v, Set<String> listOfContacts){
        this.activity = activity;
        this.contactList = contactList;
        this.v = v;
        this.listOfContacts = listOfContacts;
        notifyDataSetChanged();
    }

    public void setFilteredList(ArrayList<ContactLayout> filteredList){
        this.contactList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.UserViewHolder holder, int position) {
        ContactLayout model = contactList.get(position);

        holder.name.setText(model.getName());
        holder.phoneNumber.setText((model.getPhoneNumber()));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name, phoneNumber;
        //Set<String> listOfContacts = new HashSet<>();

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contactEditViewName);
            phoneNumber = itemView.findViewById(R.id.contactEditViewNumber);
            itemView.findViewById(R.id.addContact_Button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textName = name.getText().toString().trim();
                    String textPhoneNumber = phoneNumber.getText().toString().trim();
                    Log.d("demo", "button Clicked on contact: " + textPhoneNumber);
                    listOfContacts.add(textPhoneNumber);
                    Log.d("my check", "" + listOfContacts.toString());
                }
            });
        }
    }
}