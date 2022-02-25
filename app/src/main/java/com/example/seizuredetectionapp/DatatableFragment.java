package com.example.seizuredetectionapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatatableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatatableFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Dialog dialog;
    private String isQuestionnaireComplete;
    private Set<String> contactList = new HashSet<String>();
    private SharedPreferences sharedPreferences;

    Button btnExport, btnSettings;
    ListView journalList;
    ArrayList<String> journalInfo = new ArrayList<>();
    static ArrayAdapter adapter;
    static ArrayAdapter sortedAdapter;
    Journal journal;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout sheetBottom;
    private String currentUserUID;
    BottomSheetBehavior bottomSheetBehavior;
    private Button btnHelpRequest;
    private Spinner sortSpinner;
    private String[] sortOptions = new String[1];
    ListView sortedJournalList;
    ArrayList<String> sortedJournalInfo = new ArrayList<>();
    int pdfHeight = 1080;
    int pdfWidth = 720;
    Bitmap bmp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DatatableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatatableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatatableFragment newInstance(String param1, String param2) {
        DatatableFragment fragment = new DatatableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Retrieving the user info from shared preferences
        sharedPreferences = getActivity().getSharedPreferences(LocalSettings.PREFERENCES, Context.MODE_PRIVATE);
        isQuestionnaireComplete = sharedPreferences.getString(LocalSettings.DEFAULT, LocalSettings.questionnaireComplete);
        Log.d("boolQ", ""+isQuestionnaireComplete);



        // Checking if the user has completed the questionnaire or not
        /*
        if(isQuestionnaireComplete.equals("0")){
            showNewUserDialog();
        }
         */

        // Initializing Firebase
        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(currentUserUID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_datatable, container, false);

        //ui elements
        btnExport = root.findViewById(R.id.btnjournalExport);
        btnSettings = root.findViewById(R.id.settings);
        btnHelpRequest = root.findViewById(R.id.helpRequest);
        journalList = root.findViewById(R.id.journalList);
        sortSpinner = root.findViewById(R.id.sortSpinner);

        //Buttons
        btnExport.setOnClickListener(this);

        //item press listener
        journalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                //Dialog popup for choosing edit or remove journal
                AlertDialog.Builder editOrRemove = new AlertDialog.Builder(getContext());
                editOrRemove.setTitle("Do you want to edit or remove this journal?");
                editOrRemove.setMessage("Edit or Remove?");
                editOrRemove.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(Datatable.this, "Edited", Toast.LENGTH_SHORT).show();
                        editJournal(pos);

                    }
                });

                editOrRemove.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        //Confirmation on removing journal
                        AlertDialog.Builder confirmRemove = new AlertDialog.Builder(getContext());
                        confirmRemove.setTitle("Are you sure you want to remove this journal?");
                        confirmRemove.setMessage("Yes or No");
                        confirmRemove.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeJournal(pos);
                                Toast.makeText(getContext(), "Removed.", Toast.LENGTH_SHORT).show();

                            }
                        });
                        confirmRemove.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(), "Canceled.", Toast.LENGTH_SHORT).show();

                            }
                        });
                        confirmRemove.show();

                    }
                });
                editOrRemove.show();
            }
        });

        //spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString().trim();
                Log.d("selected Item", "selected Item" + selectedItem);

                // get a list of all the journals in firebase
                //Populate ListView
                myRef.child("Journals").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Journal journal = snapshot.getValue(Journal.class);
                        sortedJournalInfo.add(journal.dateAndTime);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // sort those journals
                Collections.sort(sortedJournalInfo);
                sortedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // send those journals to listview
        sortedAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_textformat, sortedJournalInfo);
        journalList.setAdapter(sortedAdapter);

        //listview set up
        adapter = new ArrayAdapter<>(getContext(), R.layout.listview_textformat, journalInfo);
        journalList.setAdapter(adapter);

        //Bottom Swipe up setup
        sheetBottom = root.findViewById(R.id.bottom_sheet_header);
        bottomSheetBehavior = BottomSheetBehavior.from(sheetBottom);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //Peek Height
        bottomSheetBehavior.setPeekHeight(210);

        //set journal to not be hideable
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //Populate ListView upon datatable start up
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("child added", "child added " + snapshot);
                Journal journal = snapshot.getValue(Journal.class);
                journalInfo.add(journal.dateAndTime);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }

    //remove single journal from firebase
    public void removeJournal(int pos){

        //gets key id for chosen journal
        Query query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalInfo.get(pos));

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
        journalInfo.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private void sortJournals(){
        String selectedSortOption = sortSpinner.getSelectedItem().toString().trim();
        ArrayList<String> journalInfo = new ArrayList<>();

        //Populate ListView
        myRef.child("Journals").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Journal journal  = snapshot.getValue(Journal.class);
                journalInfo.add(journal.dateAndTime);
                Log.d("journals", journal.toString());
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void editJournal(int pos){
        //create new AddJournal intent and pass the dateAndTime to the newly created activity
        Intent intent = new Intent(getContext(), AddJournal.class);
        intent.putExtra("key", true);
        Query query = myRef.child("Journals").orderByChild("dateAndTime").equalTo(journalInfo.get(pos));

        intent.putExtra("id", journalInfo.get(pos));
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case(R.id.btnjournalExport):
                try {
                    createPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DatatableFragment.this.getContext(), "PDF Upload Failed.", Toast.LENGTH_LONG).show();
                }
                break;
            case(R.id.settings):
                intent = new Intent(getContext(), MainSettings.class);
                startActivity(intent);
                break;
            case(R.id.helpRequest):
                intent = new Intent(getContext(), AlertPage.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * method for displaying the new user dialog
     */
    private void showNewUserDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_newuser_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Button Cancel = dialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), QuestionnairePersonal.class));
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginPage.class));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createPdf() throws IOException{
        //create pdf document
        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        //set pdf height and width
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pdfWidth,pdfHeight,1).create();

        //start pdf page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        //set size of text
        title.setTextSize(20);

        canvas.drawText("Logged Journals", 100, 200, title);

        //close pdf page
        document.finishPage(page);

        //downloads directory
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

        try{
            //save file to downloads directory
            document.writeTo(new FileOutputStream(file));

        }catch(IOException e){
            Toast.makeText(DatatableFragment.this.getContext(), "PDF Upload Failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
        document.close();
    }
}