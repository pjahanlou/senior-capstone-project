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

public class UpdateContactAdapter extends ArrayAdapter<UpdateContactLayout> {

    private Context mContext;
    int mResource;
    public UpdateContactAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UpdateContactLayout> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String number = getItem(position).getNumber();
        Log.d("number", ""+number);

        UpdateContactLayout contactLayout = new UpdateContactLayout(number);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView contactNumber = (TextView) convertView.findViewById(R.id.updatecontactEditViewNumber);

        contactNumber.setText(number);

        return convertView;
    }
}
