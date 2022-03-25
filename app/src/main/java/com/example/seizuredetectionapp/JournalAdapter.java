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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JournalAdapter extends ArrayAdapter<JournalLayout> {
    private Context mContext;
    int mResource;
    FirebaseDatabase database;
    DatabaseReference myRef;
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("Delete Operation", "onCancelled", databaseError.toException());
                                    }
                                });
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


}
