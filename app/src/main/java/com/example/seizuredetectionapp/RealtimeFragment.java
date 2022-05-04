package com.example.seizuredetectionapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.txusballesteros.SnakeView;

import java.util.ArrayList;

public class RealtimeFragment extends Fragment implements View.OnClickListener {
    Button btnEDA;
    Button btnMM;
    Button btnHR;
    private Dialog dialog;
    TextView txtReading, txtType;
    long lineCount;
    private String currentUserID;

//    LineChart lineChart;
//    LineData lineData;
//    LineDataSet lineDataSet;
//    ArrayList lineEntries;
    private ImageView hintImage;
    private TextView textBox, titleBox;

    private SnakeView snakeView;
    private float maxValue;

    // SET TO FALSE WHEN BACKEND IS READY
    static boolean useDummyData = false;

    public enum GraphType {
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

        txtReading = root.findViewById(R.id.txtCircle);
        txtType = root.findViewById(R.id.txtType);

        lineCount = 1;
//        lineChart = root.findViewById(R.id.lineChart);
//        lineChart.getAxisLeft().setDrawGridLines(false);
//        lineChart.getXAxis().setDrawGridLines(false);
//        lineChart.getAxisRight().setDrawGridLines(false);
//        lineEntries = new ArrayList<>();
//        lineDataSet = new LineDataSet(lineEntries, "Vitals");
//        lineData = new LineData(lineDataSet);
//        lineChart.setData(lineData);
//        lineDataSet.setColors(Color.parseColor("#FFFFFF"));
//        lineDataSet.setValueTextColor(Color.BLACK);
//        lineDataSet.setValueTextSize(18f);
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setLineWidth(3.f);
//        XAxis x = lineChart.getXAxis();
//        x.setDrawLabels(false);
//
//        graphType = GraphType.GraphType_EDA;
//
//        Description desc = new Description();
//        desc.setText("Electrodermal Activity");
//        desc.setTextSize(21.f);
//        lineChart.setDescription(desc);

        maxValue = 255.f;
        snakeView = root.findViewById(R.id.snake);
        snakeView.setMaximumNumberOfValues(30);
        snakeView.setMinValue(0.f);
        snakeView.setMaxValue(255.f);

        txtReading.setText("Calibrating");

        clearCache(GraphType.GraphType_EDA);
        clearCache(GraphType.GraphType_MM);
        clearCache(GraphType.GraphType_HR);

        getData(true);

        return root;
    }

    @Override
    public void onClick(View view) {
        String s = "Electrodermal Activity";

        switch (view.getId()) {
            case R.id.btnshowEDA:
                if (graphType == GraphType.GraphType_EDA) {
                    return;
                }
                graphType = GraphType.GraphType_EDA;
                s = "Electrodermal Activity";
                maxValue = 100.f;
                break;

            case R.id.btnshowHR:
                if (graphType == GraphType.GraphType_HR) {
                    return;
                }
                graphType = GraphType.GraphType_HR;
                s = "Heart Rate";
                maxValue = 255.f;
                break;

            case R.id.btnshowMM:
                if (graphType == GraphType.GraphType_MM) {
                    return;
                }
                graphType = GraphType.GraphType_MM;
                s = "Movement Magnitude";
                maxValue = 100.f;
                break;
            case R.id.hintRealtime:
                showHint(view.getContext());
                return;
        }
        clearCache(graphType);
        snakeView.clear();
        snakeView.setMaxValue(maxValue);
        txtType.setText(s);
        getData(false);
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

        gotIt.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void getData(boolean rfrsh) {
        // We closed, go home
        if (getContext() == null) {
            return;
        }
        getEntries();

//        lineChart.notifyDataSetChanged();
//        lineChart.invalidate();
//        lineChart.setData(lineData);
        if (rfrsh) {
            refresh(1000);
        }
    }

    private void refresh(int milliseconds) {
        if (milliseconds <= 0) {
            getData(true);
            return;
        }
        final Handler handler = new Handler();
        final Runnable runnable = () -> getData(true);

        handler.postDelayed(runnable, milliseconds);
    }

    // TODO; retrieve real-time data (need to refactor backend first) -John
    private void getEntries() {
        if (useDummyData) {
//            Random r = new Random();
//            if (lineEntries.isEmpty()) {
//                for (int i = 1; i <= 30; ++i) {
//                    Entry e = new Entry(i, r.nextFloat() * 30.f);
//                    lineEntries.add(e);
//                    lineCount++;
//                }
//            } else {
//                while (lineEntries.size() < 30) {
//                    Entry e = new Entry(lineEntries.size()+1, r.nextFloat() * 30.f);
//                    lineEntries.add(e);
//                    lineCount++;
//                }
//
//                lineDataSet.removeFirst();
//                lineDataSet.addEntry(new Entry(lineCount, r.nextFloat() * 30.f));
//                lineCount++;
//            }
        } else {
            ArrayList<CachedData.CacheNode> nodelist = CachedData.listForGraphType(graphType);

//            if (nodelist.size() == 0) {
//                return;
//            }

//            Random r = new Random();
//            if (lineEntries.isEmpty()) {
//                for (int i = 1; i <= 30; ++i) {
//                    Entry e = new Entry(i, r.nextFloat() * 30.f);
//                    lineEntries.add(e);
//                    lineCount++;
//                }
//            } else {
//                while (lineEntries.size() < 30) {
//                    Entry e = new Entry(lineEntries.size()+1, r.nextFloat() * 30.f);
//                    lineEntries.add(e);
//                    lineCount++;
//                }
//
//                lineDataSet.removeFirst();
//                lineDataSet.addEntry(new Entry(lineCount, r.nextFloat() * 30.f));
//                lineCount++;
//            }

            if (nodelist.size() == 0) {
                txtReading.setText("Calibrating");
            } else {
                CachedData.CacheNode node = nodelist.get(nodelist.size() - 1);
                String unit = "";
                switch (graphType) {
                    case GraphType_EDA:
                        unit = "EDR";
                        break;
                    case GraphType_HR:
                        unit = "bpm";
                        break;
                    case GraphType_MM:
                        unit = "m/s^2";
                        break;
                }
                txtReading.setText(Math.round(node.value) + "\n" + unit);
            }

            for (int i = 0; i < nodelist.size(); ++i) {
                CachedData.CacheNode node = nodelist.get(i);
                if (node.exists) {
                    continue;
                }
                float val = node.value;
                if (val > maxValue) {
                    val = maxValue;
                }
                else if (val < 0) {
                    val = 0;
                }
                snakeView.addValue(Math.round(val));
                node.exists = true;
                nodelist.set(i, node);
            }

//            nodelist.clear();
        }
    }

    private void clearCache(GraphType type) {
        ArrayList<CachedData.CacheNode> list = CachedData.listForGraphType(type);
        for (int i = 0; i < list.size(); ++i) {
            CachedData.CacheNode n = list.get(i);
            n.exists = false;
            list.set(i, n);
        }
    }
}