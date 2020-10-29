package com.example.firestoredatabase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.MyTimeSlotMarkerAdapter;
import com.example.firestoredatabase.Interfaces.ITimeSlotLoadListener;
import com.example.firestoredatabase.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class LoadTimeMarkerActivity extends AppCompatActivity implements ITimeSlotLoadListener, DatePickerDialog.OnDateSetListener {

    DocumentReference centreRef;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;
    LocalBroadcastManager localBroadcastManager;

    com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog;

    private RecyclerView recyclerViewSlot;
    private TextView mDate, info;
    private CardView mCard;
    SimpleDateFormat simpleDateFormat;
    public static String  judetIntent, centru, adresa;
    public static Integer  slotPosition, tempPos;
    private Button confirm, cancel;
    private  static Calendar calendar;

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer position = intent.getIntExtra("position", -1);
            slotPosition = position;
            //Toast.makeText(LoadTimeMarkerActivity.this, position.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_time_marker);

        judetIntent = getIntent().getExtras().getString("judet");
        centru = getIntent().getExtras().getString("centru");
        adresa = getIntent().getExtras().getString("adresa");

        mDate = findViewById(R.id.tvDate);
        mCard = findViewById(R.id.cardDate);
        confirm = findViewById(R.id.btn_confirmBooking);
        cancel = findViewById(R.id.btn_cancel);
        info = findViewById(R.id.info);
        info.setText(judetIntent + " / " + centru + "\n" + adresa);
        recyclerViewSlot = findViewById(R.id.recycler_slot);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mMessageReceiver,
                new IntentFilter("positionSlot"));

        simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");

        calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY){
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            mDate.setText(simpleDateFormat.format(calendar.getTime()));
            Common.currentDate = calendar;
        }
        else if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY
                || dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            mDate.setText(simpleDateFormat.format(calendar.getTime()));
            Common.currentDate = calendar;
        }
        else if(dayOfWeek == Calendar.FRIDAY)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            mDate.setText(simpleDateFormat.format(calendar.getTime()));
            Common.currentDate = calendar;
        }

        recyclerViewSlot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(LoadTimeMarkerActivity.this, 3);
        recyclerViewSlot.setLayoutManager(gridLayoutManager);
        recyclerViewSlot.addItemDecoration(new SpacesItemDecoration(8));

        iTimeSlotLoadListener = this;
        dialog = new SpotsDialog.Builder().setContext(LoadTimeMarkerActivity.this).setCancelable(false).build();

        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
        loadAvailableTimeSlot(centru, sdf.format(Common.currentDate.getTime()));

        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoadTimeMarkerActivity.this, FinalMapActivity.class));
                slotPosition = null;
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slotPosition == null || slotPosition == 97)
                {
                    Toast.makeText(LoadTimeMarkerActivity.this, "Nu ați ales niciun slot.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    tempPos = slotPosition;
                    slotPosition = null;
                    Intent intent = new Intent(LoadTimeMarkerActivity.this, MarkerBookingActivity.class);
                    intent.putExtra("slotNew", tempPos);
                    intent.putExtra("centreId", centru);
                    intent.putExtra("judet", judetIntent);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void showDataDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 60);

        int Year, Month, Day;
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = DatePickerDialog.newInstance(LoadTimeMarkerActivity.this, Year, Month, Day);

        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Data");

        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setMaxDate(endDate);

        for (Calendar loopdate = calendar; calendar.before(endDate); calendar.add(Calendar.DATE, 1), loopdate = calendar) {
            int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar[] disabledDays =  new Calendar[1];
                disabledDays[0] = loopdate;
                datePickerDialog.setDisabledDays(disabledDays);
            }
        }
        datePickerDialog.show(getFragmentManager(),"Alege Data");
    }

    private void loadAvailableTimeSlot(String centreId, final String format) {
        dialog.show();

        //centreId = "\"WC5gQnQFyIHHBJu1aM5b\"";
        ///donationLocation/Bucuresti/NewBranch/WC5gQnQFyIHHBJu1aM5b
        if(!TextUtils.isEmpty(judetIntent)) {
            centreRef = FirebaseFirestore.getInstance()
                    .collection("donationLocation")
                    .document(judetIntent)
                    .collection("NewBranch")
                    .document(centreId);
        }

        centreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists())
                    {
                        //get info about booking, else return empty
                        CollectionReference date = FirebaseFirestore.getInstance()
                                .collection("donationLocation")
                                .document(judetIntent)
                                .collection("NewBranch")
                                .document(centreId)
                                .collection(format); //book date

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(querySnapshot.isEmpty())
                                    {
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    }
                                    else
                                    {
                                        List<TimeSlot> timeSlots = new ArrayList<>();
                                        for(QueryDocumentSnapshot documentSnapshot1:task.getResult())
                                            timeSlots.add(documentSnapshot1.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                    else
                    {
                        onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotMarkerAdapter adapter = new MyTimeSlotMarkerAdapter(LoadTimeMarkerActivity.this, timeSlotList);
        recyclerViewSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(LoadTimeMarkerActivity.this, message, Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotMarkerAdapter adapter = new MyTimeSlotMarkerAdapter(LoadTimeMarkerActivity.this);
        recyclerViewSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slotPosition = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 60);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd MMM YYYY");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd_MM_YYYY");
        mDate.setText(simpleDateFormat2.format(calendar.getTime()));
        Common.currentDate = calendar;
        loadAvailableTimeSlot(centru, sdf2.format(calendar.getTime()));
    }
}
