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
import androidx.recyclerview.widget.RecyclerView;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.UserViewHolder>{
    public ArrayList<String> listOfContacts;
    View v;
    Activity activity;
    ArrayList<ContactLayout> contactList;

    public ContactsAdapter(Activity activity, ArrayList<ContactLayout> contactList, View v, ArrayList<String> listOfContacts){
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

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name, phoneNumber;

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
                    if(listOfContacts.contains(textPhoneNumber) == false){
                        listOfContacts.add(textPhoneNumber);
                    }
                }
            });
        }
    }
}