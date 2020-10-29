package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoBookingActivity extends AppCompatActivity {

    LinearLayout l1;
    ImageView img1;
    TextView tv;
    Button btn;
    Animation updown, downtoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_booking);

        l1 = findViewById(R.id.heartId);
        img1 = findViewById(R.id.img1);
        tv = findViewById(R.id.info);
        btn = findViewById(R.id.button6);
        updown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setAnimation(updown);
        l1.setAnimation(downtoup);
        btn.setAnimation(downtoup);
        tv.setAnimation(updown);
        img1.setAnimation(updown);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoBookingActivity.this, HomeActivity.class));
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoBookingActivity.this, ReservationActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
