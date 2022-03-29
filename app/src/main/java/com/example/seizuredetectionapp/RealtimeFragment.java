package com.example.seizuredetectionapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

public class RealtimeFragment extends Fragment implements View.OnClickListener {
    Button btnEDA;
    Button btnMM;
    GraphView graphView;
    private Dialog dialog;

    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;

    enum GraphType {
        GraphType_EDA,
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
//        graphView = root.findViewById(R.id.idGraphView);
        lineChart = root.findViewById(R.id.lineChart);
        lineEntries = new ArrayList<>();
        getEntries();
        lineDataSet = new LineDataSet(lineEntries, "");
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(18f);

        graphType = GraphType.GraphType_EDA;
        updateGraph(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_realtime, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnshowEDA:
                graphType = GraphType.GraphType_EDA;
                break;

            case R.id.btnshowMM:
                graphType = GraphType.GraphType_MM;
                break;
        }
        updateGraph(false);
    }

    private void updateGraph(boolean rfrsh) {
        String s = "Something broke";
        switch (graphType) {
            case GraphType_EDA:
                s = "Electrodermal Activity";
                break;
            case GraphType_MM:
                s = "Movement Magnitude";
                break;
        }

//        Random r = new Random();
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
//
//        for (int i = 0; i < 60; ++i) {
//            series.appendData(new DataPoint(i, r.nextInt(100)), true, 60);
//        }
//
//        graphView.setTitle(s);
//        graphView.setTitleColor(R.color.purple_200);
//        graphView.setTitleTextSize(18);
//        graphView.addSeries(series);

        getEntries();
        lineChart.setData(lineData);

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

    private void getEntries() {
        Random r = new Random();
        lineEntries.add(new Entry(1f, r.nextInt(10)));
        lineEntries.add(new Entry(2f, r.nextInt(10)));
        lineEntries.add(new Entry(3f, r.nextInt(10)));
        lineEntries.add(new Entry(4f, r.nextInt(10)));
        lineEntries.add(new Entry(5f, r.nextInt(10)));
        lineEntries.add(new Entry(6f, r.nextInt(10)));
    }
}