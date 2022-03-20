package com.example.seizuredetectionapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
        ImageButton ibThreeDots = (ImageButton) convertView.findViewById(R.id.threeDots);

        tvDateTime.setText(dateAndTime);
        tvDuration.setText(duration);
        tvDescription.setText(description);

        ibThreeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, view, Gravity.END);
                MenuInflater inflater = popup.getMenuInflater();
                //inflate with view
                inflater.inflate(R.menu.journal_three_dots_menu, popup.getMenu());
                //set menu item click listener here
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, journal));
                popup.show();
            }
        });
        return convertView;
    }

}
