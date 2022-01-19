package com.example.seizuredetectionapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Viewer> {
    View v;
    Activity activity;
    ArrayList<ContactLayout> contactList;

    public MainAdapter(Activity activity, ArrayList<ContactLayout> contactList, View v, ImageButton btn){
        this.activity = activity;
        this.contactList = contactList;
        this.v = v;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainAdapter.Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new Viewer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.Viewer holder, int position) {
        ContactLayout model = contactList.get(position);

        holder.name.setText(model.getName());
        holder.phoneNumber.setText((model.getPhoneNumber()));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class Viewer extends RecyclerView.ViewHolder {
        TextView name, phoneNumber;
        Button addContact_Button;

        public Viewer(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.contactEditViewName);
            phoneNumber = itemView.findViewById(R.id.contactEditViewNumber);
        }
    }
}
