package com.example.firestoredatabase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.firestoredatabase.Adapter.MyViewPagerAdapter;
import com.example.firestoredatabase.CommonJavaClass.NoSwipeViewpager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollRef = db.collection("users");
    private DocumentReference userRef = db.document("users/" + mFirebaseAuth.getUid());

    StepView stepView;
    NoSwipeViewpager viewPager;
    Button next_step;
    Button previous_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        stepView = findViewById(R.id.step_view);
        viewPager = findViewById(R.id.view_pager);
        next_step = findViewById(R.id.btn_next_step);
        previous_step = findViewById(R.id.btn_previous_step);


        setupStepView();
        setColorButton();
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                stepView.go(position, true);
                if (position == 0)
                    previous_step.setEnabled(false);
                else
                    previous_step.setEnabled(true);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        previous_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.step == 2 || Common.step > 0) {
                    Common.step--;
                    viewPager.setCurrentItem(Common.step);
                    if (Common.step < 2) {
                        next_step.setEnabled(true);
                        setColorButton();
                    }
                }
            }
        });

        next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BookingActivity.this, "" + Common.currentJudet.getJudetId(), Toast.LENGTH_LONG).show();
                if (Common.step < 2 || Common.step == 0) {
                    Common.step++;
                    viewPager.setCurrentItem(Common.step);
                }
            }
        });
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Judet + Centru");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }

    private void setColorButton() {
        if (next_step.isEnabled()) {
            next_step.setBackgroundResource(R.color.green);
        } else {
            next_step.setBackgroundResource(R.color.colorPrimaryDark);
        }

        if (previous_step.isEnabled()) {
            previous_step.setBackgroundResource(R.color.green);
        } else {
            previous_step.setBackgroundResource(R.color.colorPrimaryDark);
        }
    }
}
