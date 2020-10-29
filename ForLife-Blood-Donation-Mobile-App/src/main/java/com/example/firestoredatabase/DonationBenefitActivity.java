package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DonationBenefitActivity extends AppCompatActivity {
    static int i = 0;
    //ImageView imageView;
    LinearLayout imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_benefit);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonationBenefitActivity.this, HomeActivity.class));
                finish();
            }
        });
        int images[] = {R.drawable.blog,
                R.drawable.helping,
                R.drawable.bloddy,
                R.drawable.child,
                R.drawable.unnamed,
                R.drawable.fly,};

        imageView = findViewById(R.id.imageBack);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            public void run() {
                imageView.setBackground(getDrawable(images[i]));
                i++;
                if(i > images.length-1)
                {
                    i = 0;
                }
                handler.postDelayed(this, 4000);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0); //for initial delay..
    }
}
