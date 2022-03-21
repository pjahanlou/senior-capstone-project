package com.example.seizuredetectionapp;

import static androidx.test.InstrumentationRegistry.getContext;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
    int position;
    JournalLayout journalLayout;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public MyMenuItemClickListener(int position, JournalLayout journalLayout) {
        this.position = position;
        this.journalLayout = journalLayout;
        }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Query query;
        switch (menuItem.getItemId()){
            case R.id.editItem:
                Log.d("here", String.valueOf(position));
                Log.d("journal",journalLayout.getDateAndTime());

                Intent intent = new Intent(getContext(), AddJournal.class);
                intent.putExtra("key", true);
                query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalLayout.getDateAndTime());
                intent.putExtra("id", journalLayout.getDateAndTime());
                //startActivity(intent);


                return true;
            case R.id.deleteItem:
                Log.d("here2", String.valueOf(position));
                //gets key id for chosen journal
                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("Users").child(currentUserUID);
                query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalLayout.getDateAndTime());
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
                return true;
            default:
    }
    return false;
}


}
