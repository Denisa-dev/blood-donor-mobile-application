package com.example.firestoredatabase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.firestoredatabase.Adapter.MyViewPagerAdapter;
import com.example.firestoredatabase.CommonJavaClass.NoSwipeViewpager;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReservationActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NoSwipeViewpager viewPager;
    @BindView(R.id.btn_next_step)
    Button next_step;
    @BindView(R.id.btn_previous_step)
    Button previous_step;
    @BindView(R.id.cancel)
    CardView mCancel;

    //event

    @OnClick(R.id.cancel)
    void cancelAction(){
        resetStaticData();
        Common.tab = 0;
        startActivity(new Intent(ReservationActivity.this, HomeActivity.class));
        finish();
    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentJudet = null;
        Common.currentDate.add(Calendar.DATE, 0);
    }

    @OnClick(R.id.btn_previous_step)
    void setPrevious_step() {
        if (Common.step == 2 || Common.step > 0) {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step < 2) {
                next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick() {
        //Toast.makeText(this, "" + Common.currentJudet.getJudetId(), Toast.LENGTH_LONG).show();
        if (Common.step < 2 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) //after choosen centru
            {
                if (Common.currentJudet.getJudetId() != null)
                    loadTimePickerDate(Common.currentJudet.getJudetId());
                //loadCentreInfo(Common.currentJudet.getJudetId());
            } else if (Common.step == 2) //confirm
            {
                if (Common.currentTimeSlot != -1){
                    next_step.setVisibility(View.INVISIBLE);
                    previous_step.setVisibility(View.INVISIBLE);
                    confirmReservation();}
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmReservation() {
        Intent intent = new Intent(Common.KEY_CONFIRM);
        localBroadcastManager.sendBroadcast(intent);
    }


    private void loadTimePickerDate(String centreId) {
        //send local broadcat to frag2
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);

    }

    //broadcast receiver
    private BroadcastReceiver enableNextButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if (step == 0) {
                Common.currentJudet = intent.getParcelableExtra(Common.KEY_CENTRU);
                next_step.setEnabled(true);
                setColorButton();
            } else if (step == 1) {
                Common.currentJudet = intent.getParcelableExtra(Common.KEY_CENTRE_SELECTED);
                next_step.setEnabled(true);
                setColorButton();
            } else if (step == 2) {
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
                //Toast.makeText(ReservationActivity.this, String.valueOf(Common.currentTimeSlot), Toast.LENGTH_SHORT).show();
                next_step.setEnabled(true);
                setColorButton();
            }
            else if (step == 3) {
                //Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
                next_step.setEnabled(false);
                next_step.setVisibility(View.INVISIBLE);
                previous_step.setVisibility(View.INVISIBLE);
                setColorButton();
            }
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(enableNextButtonReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

            ButterKnife.bind(ReservationActivity.this);

            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(enableNextButtonReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

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
                    else{
                        previous_step.setEnabled(true);
                        next_step.setEnabled(false);      }
                    setColorButton();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
       // }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Județ + Centru");
        stepList.add("Data + Ora");
        stepList.add("Confirmare");
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
