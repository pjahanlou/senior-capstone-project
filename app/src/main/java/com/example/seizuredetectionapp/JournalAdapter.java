package com.example.seizuredetectionapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JournalAdapter extends ArrayAdapter<JournalLayout> {
    private Context mContext;
    int mResource;
    ArrayList<JournalLayout> mObjects;
    FirebaseDatabase database;
    DatabaseReference myRef;
    HashMap<String,String> months = new HashMap<String,String>();
    private ArrayList<Journal> updateJournals;
    Float severityFloat;

    public JournalAdapter(@NonNull Context context, int resource, @NonNull ArrayList<JournalLayout> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObjects = objects;
        //Populate hashmap of months
        months.put("01","Jan");months.put("07","Jul");
        months.put("02","Feb");months.put("08","Aug");
        months.put("03","Mar");months.put("09","Sept");
        months.put("04","Apr");months.put("10","Oct");
        months.put("05","May");months.put("11","Nov");
        months.put("06","Jun");months.put("12","Dec");

    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String dateAndTime = getItem(position).getDateAndTime();
        String duration = getItem(position).getDuration();
        String description = getItem(position).getDescription();
        String severity = getItem(position).getSeverity();

        if(severity != null && severity.length() > 0){
            severityFloat = Float.parseFloat(severity);
        }

        JournalLayout journal = new JournalLayout(dateAndTime,duration,description,severity);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //set up display
        TextView tvDay = (TextView) convertView.findViewById(R.id.lvDay);
        TextView tvMonth = (TextView) convertView.findViewById(R.id.lvMonth);
        TextView tvYear = (TextView) convertView.findViewById(R.id.lvYear);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.lvDuration);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.lvDescription);
        ImageButton ibThreeDots = (ImageButton) convertView.findViewById(R.id.threeDots);
        RangeSlider rsSeverity = (RangeSlider) convertView.findViewById(R.id.severityDisplay);

        //set slider to disabled
        rsSeverity.setEnabled(false);

        //set display with text
        tvDay.setText(getDay(dateAndTime));
        tvMonth.setText(getMonth(dateAndTime));
        tvYear.setText(getYear(dateAndTime));
        tvDuration.setText(durationSeizureConvert(Float.parseFloat(duration)));
        tvDescription.setText(description);
        rsSeverity.setValues(severityFloat);

        ibThreeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, view, Gravity.END);
                MenuInflater inflater = popup.getMenuInflater();
                //inflate with view
                inflater.inflate(R.menu.journal_three_dots_menu, popup.getMenu());
                //set menu item click listener here
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Query query;
                        switch (menuItem.getItemId()){
                            case(R.id.editItem):
                                //sets up edit journal parameters and starts AddJournal class
                                Intent i = new Intent(getContext(), AddJournal.class);
                                i.putExtra("key",true);
                                i.putExtra("id",journal.getDateAndTime());
                                mContext.startActivity(i);
                                return true;

                            case(R.id.deleteItem):
                                Log.d("here2", String.valueOf(position));
                                //gets key id for chosen journal
                                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                database = FirebaseDatabase.getInstance();
                                myRef = database.getReference("Users").child(currentUserUID);
                                query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journal.getDateAndTime());
                                //removes selected entry from firebase using the data and time as the key
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                            Toast.makeText(mContext, "Journal Deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("Delete Operation", "onCancelled", databaseError.toException());
                                    }
                                });
                                mObjects.remove(position);
                                notifyDataSetChanged();
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        return convertView;
    }

    public String getMonth(String dateTime){
        char c0 = dateTime.charAt(0);
        char c1 = dateTime.charAt(1);
        StringBuilder sb = new StringBuilder();
        sb.append(c0);
        sb.append(c1);
        String monthNum = sb.toString();
        String month = months.get(monthNum);
        return month;
    }

    public String getDay(String dateTime){
        char c3 = dateTime.charAt(3);
        char c4 = dateTime.charAt(4);
        StringBuilder sb = new StringBuilder();
        sb.append(c3);
        sb.append(c4);
        String day = sb.toString();
        return day;
    }

    public String getYear(String dateTime){
        char c6 = dateTime.charAt(6);
        char c7 = dateTime.charAt(7);
        char c8 = dateTime.charAt(8);
        char c9 = dateTime.charAt(9);
        StringBuilder sb = new StringBuilder();
        sb.append(c6);
        sb.append(c7);
        sb.append(c8);
        sb.append(c9);
        String year = sb.toString();
        return year;
    }

    public void setList(){
        this.updateJournals = updateJournals;
        notifyDataSetChanged();
    }

    private String durationSeizureConvert(float value) {
        if(value == 0){
            return "30 Sec";
        } else if(value == 120) {
            return "1 Hour";
        }else if(value == 1){
            return ((int)value)+" Min";
        } else if(value % 2 == 1){
            return ((int)value/2)+" Min 30 Sec";
        }  else{
            return ((int)value/2)+" Min";
        }
    }

}
