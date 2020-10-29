package com.example.firestoredatabase;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PopupIstoricActivity extends AppCompatActivity {

    public static String centru, date, time;
    TextView tvCentru, tvOra, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_istoric);

        centru = getIntent().getExtras().getString("centreId");
        date = getIntent().getExtras().getString("date");
        time = getIntent().getExtras().getString("time");

        tvCentru = findViewById(R.id.text_centru);
        tvOra = findViewById(R.id.time_centru);
        tvDate = findViewById(R.id.date_centru);

        tvDate.setText(date);
        tvOra.setText(time);
        tvCentru.setText(centru);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .9), (int)(height *.4));
    }
}
