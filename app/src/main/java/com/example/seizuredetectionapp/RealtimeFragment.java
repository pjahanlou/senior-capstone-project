package com.example.seizuredetectionapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class RealtimeFragment extends Fragment implements View.OnClickListener {
    Button btnEDA;
    Button btnMM;
    Button btnHR;
    private Dialog dialog;
    TextView reading;
    long lineCount;
    private String currentUserID;

    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    private ImageView hintImage;
    private TextView textBox, titleBox;

    // SET TO FALSE WHEN BACKEND IS READY
    static boolean useDummyData = true;

    enum GraphType {
        GraphType_EDA,
        GraphType_HR,
        GraphType_MM
    }

    private GraphType graphType;

    public RealtimeFragment() {
        graphType = GraphType.GraphType_EDA;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_newuser_dialog);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_bg));
//        }
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false); //Optional
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_realtime, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Buttons
        btnEDA = root.findViewById(R.id.btnshowEDA);
        btnHR = root.findViewById(R.id.btnshowHR);
        btnMM = root.findViewById(R.id.btnshowMM);
        hintImage = root.findViewById(R.id.hintRealtime);
        btnEDA.setOnClickListener(this);
        btnHR.setOnClickListener(this);
        btnMM.setOnClickListener(this);
        hintImage.setOnClickListener(this);

        reading = root.findViewById(R.id.txtCircle);

        lineCount = 2;
        lineChart = root.findViewById(R.id.lineChart);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineEntries = new ArrayList<>();
        getEntries();
        createChart("Electrodermal Activity");

        graphType = GraphType.GraphType_EDA;
        updateGraph(true);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnshowEDA:
                if (graphType == GraphType.GraphType_EDA) {
                    return;
                }
                graphType = GraphType.GraphType_EDA;
                break;

            case R.id.btnshowHR:
                if (graphType == GraphType.GraphType_HR) {
                    return;
                }
                graphType = GraphType.GraphType_HR;
                break;

            case R.id.btnshowMM:
                if (graphType == GraphType.GraphType_MM) {
                    return;
                }
                graphType = GraphType.GraphType_MM;
                break;
            case R.id.hintRealtime:
                showHint(view.getContext());
                break;
        }
        updateGraph(false);
    }

    private void showHint(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.profile_settings_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        textBox = dialog.getWindow().findViewById(R.id.textView2);
        titleBox = dialog.getWindow().findViewById(R.id.textView);
        textBox.setText("This page displays the data your phone receives from your wearable device. To change which type of data you are viewing click the corresponding button at the bottom!");
        titleBox.setText("Realtime Data Page");

        Button gotIt = dialog.findViewById(R.id.btn_gotit);

        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateGraph(boolean rfrsh) {
        String s = "Something broke";
        switch (graphType) {
            case GraphType_EDA:
                s = "Electrodermal Activity";
                break;
            case GraphType_HR:
                s = "Heart Rate";
                break;
            case GraphType_MM:
                s = "Movement Magnitude";
                break;
        }

        getEntries();
        createChart(s);

        if (useDummyData) {

            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Entry e = (Entry) lineEntries.get(29);
            float y = e.getY();
            y = Math.round(y  * 10.f) / 10.f;
            reading.setText(String.valueOf(y));
            lineChart.setData(lineData);
        }

        if (rfrsh) {
            refresh(1000);
        }
    }

    private void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateGraph(true);
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    // TODO; retrieve real-time data (need to refactor backend first) -John
    private void getEntries() {
        if (useDummyData) {
            Random r = new Random();
            if (lineEntries.isEmpty()) {
                for (int i = 1; i <= 30; ++i) {
                    Entry e = new Entry(i, r.nextFloat() * 30.f);
                    lineEntries.add(e);
                    lineCount++;
                }
            } else {
                while (lineEntries.size() < 30) {
                    Entry e = new Entry(lineEntries.size()+1, r.nextFloat() * 30.f);
                    lineEntries.add(e);
                    lineCount++;
                }

                lineDataSet.removeFirst();
                lineDataSet.addEntry(new Entry(lineCount-1, r.nextFloat() * 30.f));
                lineCount++;
            }
        } else {
            String s = "eda";
            switch (graphType) {
                case GraphType_EDA:
                    s = "eda";
                    break;
                case GraphType_HR:
                    s = "hr";
                    break;
                case GraphType_MM:
                    s = "acc";
                    break;
            }

            requestData(s);
        }
    }

    private void requestData(String dataType) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userkey").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String key = "?key=" + snapshot.getValue();
                        String after = "&after=" + String.valueOf(System.currentTimeMillis() / 1000L - 30);
                        Log.d("aaa", HTTPHelpers.MYURL + dataType + key + after);
                        RequestQueue queue = Volley.newRequestQueue(getContext());
                        queue.start();
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, HTTPHelpers.MYURL + dataType + key + after,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("aaa", response);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Volley Error", error.toString());
                            }
                        });
                        queue.add(stringRequest);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createChart(String graphStr) {
        if (lineChart.getLineData() != null) {
            lineChart.clearValues();
        }
        lineDataSet = new LineDataSet(lineEntries, "Vitals");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(getResources().getColor(R.color.purple_500));
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(3.f);
        XAxis x = lineChart.getXAxis();
        x.setDrawLabels(false);

        Description desc = new Description();
        desc.setText(graphStr);
        desc.setTextSize(21.f);
        lineChart.setDescription(desc);
    }
}