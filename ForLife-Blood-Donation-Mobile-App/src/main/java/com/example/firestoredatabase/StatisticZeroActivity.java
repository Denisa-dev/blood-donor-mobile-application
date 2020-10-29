package com.example.firestoredatabase;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatisticZeroActivity extends AppCompatActivity {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection("donationLocation");
    AnyChartView myChartView;
    ProgressBar progressBar;
    private static Integer[] noBookingsF;
    private static Integer[] noBookingsM;
    private static String[] judet;
    private static final String TAG = "StatisticTotalFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_zero);
        myChartView = findViewById(R.id.any_chart_view_total);
        progressBar = findViewById(R.id.progressBar2);

        myChartView.setProgressBar(progressBar);

        judet = new String[41];
        noBookingsF = new Integer[judet.length];
        noBookingsM = new Integer[judet.length];
        Log.d(TAG, String.valueOf(judet.length));
        Common.noBookingsF = new int[judet.length];
        Common.noBookingsM = new int[judet.length];
        pieChart();
    }

    private void pieChart() {
        Pie pie = AnyChart.pie();
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Integer index = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        judet[index] = document.getId();
                        noBookingsF[index] = Integer.parseInt(document.get("noBookingsF").toString());
                        Common.noBookingsF[index] = noBookingsF[index];
                        noBookingsM[index] = Integer.parseInt(document.get("noBookingsM").toString());
                        Common.noBookingsM[index] = noBookingsM[index];
                        Log.d(TAG, noBookingsF[index].toString() + " " + noBookingsM[index]);
                        index++;
                    }
                    List<DataEntry> data = new ArrayList<>();
                    for (int i = 0; i < judet.length; i++) {
                        data.add(new ValueDataEntry(judet[i], noBookingsF[i] + noBookingsM[i]));
                    }
                    pie.data(data);
                    pie.title("Număr total de donatori pe țară 2020");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Statistică Pie-Chart").padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    myChartView.setChart(pie);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
