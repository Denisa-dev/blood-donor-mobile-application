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
import com.anychart.charts.Cartesian;
import com.anychart.core.axes.Linear;
import com.anychart.core.cartesian.series.Bar;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LabelsOverlapMode;
import com.anychart.enums.Orientation;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StatisticUnuActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_statistic_unu);

        progressBar = findViewById(R.id.progressBar2);
        myChartView = findViewById(R.id.any_chart_view_sex);
        //myChartView.setProgressBar();
        myChartView.setProgressBar(progressBar);
        judet = new String[41];
        noBookingsF = new Integer[judet.length];
        noBookingsM = new Integer[judet.length];
        Log.d(TAG, String.valueOf(judet.length));
        barChart();
    }

    private void barChart() {
        Cartesian barChart = AnyChart.bar();
        barChart.animation(true);
        barChart.padding(10d, 20d, 5d, 20d);
        barChart.yScale().stackMode(ScaleStackMode.VALUE);

        barChart.yAxis(0d).title("Număr rezervări");
        barChart.xAxis(0d).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        Linear xAxis1 = barChart.xAxis(1d);
        xAxis1.enabled(true);
        xAxis1.orientation(Orientation.RIGHT);
        xAxis1.overlapMode(LabelsOverlapMode.ALLOW_OVERLAP);

        barChart.title("Număr de donatori pe sexe în județe 2020");

        barChart.interactivity().hoverMode(HoverMode.BY_X);

        barChart.tooltip()
                .title(false)
                .separator(false)
                .displayMode(TooltipDisplayMode.SEPARATED)
                .positionMode(TooltipPositionMode.POINT)
                .useHtml(true)
                .fontSize(12d)
                .offsetX(5d)
                .offsetY(0d);


        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Integer index = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        judet[index] = document.getId();
                        noBookingsF[index] = Integer.parseInt(document.get("noBookingsF").toString());
                        noBookingsM[index] = Integer.parseInt(document.get("noBookingsM").toString());
                        Log.d(TAG, noBookingsF[index].toString() + " " + noBookingsM[index]);
                        index++;
                    }
                    List<DataEntry> seriesData = new ArrayList<>();
                    for (int i = 0; i < judet.length; i++){
                        seriesData.add(new CustomDataEntry(judet[i], noBookingsF[i], noBookingsM[i]));
                    }

                    Set set = Set.instantiate();
                    set.data(seriesData);
                    Mapping series1Data = set.mapAs("{ x: 'x', value: 'value' }");
                    Mapping series2Data = set.mapAs("{ x: 'x', value: 'value2' }");

                    Bar series1 = barChart.bar(series1Data);
                    series1.name("Feminin")
                            .color("HotPink");
                    series1.tooltip()
                            .position("left")
                            .anchor(Anchor.RIGHT_CENTER);

                    Bar series2 = barChart.bar(series2Data);
                    series2.name("Masculin");
                    series2.tooltip()
                            .position("right")
                            .anchor(Anchor.LEFT_CENTER);

                    barChart.legend().enabled(true);
                    barChart.legend().inverted(true);
                    barChart.legend().fontSize(13d);
                    barChart.legend().padding(0d, 0d, 20d, 0d);

                    myChartView.setChart(barChart);
                }
            }
        });
    }
    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
