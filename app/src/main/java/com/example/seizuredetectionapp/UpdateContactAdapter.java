package com.example.seizuredetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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

        UpdateContactLayout updateContact = new UpdateContactLayout(number);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView numTextView = (TextView) convertView.findViewById(R.id.contactNum);

        numTextView.setText(number);

        return convertView;
    }
}
