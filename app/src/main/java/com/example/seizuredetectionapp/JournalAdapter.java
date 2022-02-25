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

public class JournalAdapter extends ArrayAdapter<JournalLayout> {
    private Context mContext;
    int mResource;
    public JournalAdapter(@NonNull Context context, int resource, @NonNull ArrayList<JournalLayout> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String dateAndTime = getItem(position).getDateAndTime();
        String duration = getItem(position).getDuration();
        String description = getItem(position).getDescription();

        JournalLayout journal = new JournalLayout(dateAndTime,duration,description);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvDateTime = (TextView) convertView.findViewById(R.id.lvDateAndTime);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.lvDuration);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.lvDescription);

        tvDateTime.setText(dateAndTime);
        tvDuration.setText(duration);
        tvDescription.setText(description);

        return convertView;
    }
}
