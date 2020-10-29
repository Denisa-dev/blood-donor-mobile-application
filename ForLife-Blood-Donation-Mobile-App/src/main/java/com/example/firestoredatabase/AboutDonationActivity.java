package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ms.square.android.expandabletextview.ExpandableTextView;

public class AboutDonationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_donation);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AboutDonationActivity.this, HomeActivity.class));
                finish();
            }
        });

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.question1);
        ExpandableTextView expTv2 = (ExpandableTextView) findViewById(R.id.question2);
        ExpandableTextView expTv3 = (ExpandableTextView) findViewById(R.id.question3);
        ExpandableTextView expTv4 = (ExpandableTextView) findViewById(R.id.question4);
        ExpandableTextView expTv5 = (ExpandableTextView) findViewById(R.id.question5);
        ExpandableTextView expTv6 = (ExpandableTextView) findViewById(R.id.question6);
        ExpandableTextView expTv7 = (ExpandableTextView) findViewById(R.id.question7);
        ExpandableTextView expTv8 = (ExpandableTextView) findViewById(R.id.question8);
        ExpandableTextView expTv9 = (ExpandableTextView) findViewById(R.id.question9);
        ExpandableTextView expTv10 = (ExpandableTextView) findViewById(R.id.question10);
        ExpandableTextView expTv11 = (ExpandableTextView) findViewById(R.id.question11);
        ExpandableTextView expTv12 = (ExpandableTextView) findViewById(R.id.question12);
        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(getText(R.string.question1));
        expTv2.setText(getText(R.string.question2));
        expTv3.setText(getText(R.string.question3));
        expTv4.setText(getText(R.string.question4));
        expTv5.setText(getText(R.string.question5));
        expTv6.setText(getText(R.string.question6));
        expTv7.setText(getText(R.string.question7));
        expTv8.setText(getText(R.string.question8));
        expTv9.setText(getText(R.string.question9));
        expTv10.setText(getText(R.string.question10));
        expTv11.setText(getText(R.string.question11));
        expTv12.setText(getText(R.string.question12));
    }
}
