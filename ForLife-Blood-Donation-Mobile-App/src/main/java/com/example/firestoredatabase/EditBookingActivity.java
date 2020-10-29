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

import com.example.firestoredatabase.Adapter.MyTimeEditSlotAdapter;
import com.example.firestoredatabase.Interfaces.ITimeEditSlotLoadListener;
import com.example.firestoredatabase.Model.TimesEditSlot;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EditBookingActivity extends AppCompatActivity implements ITimeEditSlotLoadListener, DatePickerDialog.OnDateSetListener {

    DocumentReference centreRef;
    ITimeEditSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;
    LocalBroadcastManager localBroadcastManager, localBroadcastFlag;

    com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog;

    private  RecyclerView recyclerViewSlot;
    private TextView mDate;
    private CardView mCard;
    SimpleDateFormat simpleDateFormat;
    public static String slotIntent, dateIntent, judetIntent, centru, day;
    public static Integer mDay, slotPosition, slotPos = 22, tempSlot;
    public static Date dateLong;
    private Button confirm, cancel;
    private  static  Calendar calendar;

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer position = intent.getIntExtra("position", -1);
            slotPosition = position;
            //Toast.makeText(EditBookingActivity.this, position.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public BroadcastReceiver mFlagReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flagPos = intent.getIntExtra("sendFlag", -1);
            slotPos = flagPos;
            //Toast.makeText(EditBookingActivity.this, position.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);
        slotIntent = getIntent().getExtras().getString("slot");
        //Toast.makeText(EditBookingActivity.this, slotIntent.toString(), Toast.LENGTH_LONG).show();
        dateIntent = getIntent().getExtras().getString("date");

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mMessageReceiver,
                new IntentFilter("sendPosition"));

        localBroadcastFlag = LocalBroadcastManager.getInstance(this);
        localBroadcastFlag.registerReceiver(mFlagReceiver,
                new IntentFilter("sendFlag"));

        day = dateIntent.substring(0, 2);
        if(day.startsWith("0"))
        {
             mDay = Integer.parseInt(day.substring(1, 2));
        }
        else
        {
            mDay = Integer.parseInt(day);
        }

        judetIntent = getIntent().getExtras().getString("judet");
        centru = getIntent().getExtras().getString("centreId");

        recyclerViewSlot = findViewById(R.id.recycler_slot);

        mDate = findViewById(R.id.tvDate);
        mCard = findViewById(R.id.cardDate);
        confirm = findViewById(R.id.btn_confirmBooking);
        cancel = findViewById(R.id.btn_cancel);

        iTimeSlotLoadListener = this;
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        dialog = new SpotsDialog.Builder().setContext(EditBookingActivity.this).setCancelable(false).build();

        mDate.setText(getIntent().getExtras().getString("dateLong"));

        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            calendar.setTime(sdf.parse(getIntent().getExtras().getString("calendar")));
            Common.currentDate = calendar;
            Common.actualDate = calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        recyclerViewSlot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(EditBookingActivity.this, 3);
        recyclerViewSlot.setLayoutManager(gridLayoutManager);
        recyclerViewSlot.addItemDecoration(new SpacesItemDecoration(8));

        loadAvailableTimeSlot(centru, dateIntent);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slotPosition == null || slotPosition == 98 || slotPos == 97)
                {
                    Toast.makeText(EditBookingActivity.this, "Nu ați ales niciun slot nou din cele libere.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    tempSlot = slotPosition;
                    slotPosition = null;
                    Intent intent = new Intent(EditBookingActivity.this, EditBookingPreviewActivity.class);
                    intent.putExtra("slotNew", tempSlot);
                    intent.putExtra("slotOld", slotIntent);
                    intent.putExtra("date", dateIntent);
                    intent.putExtra("centreId", centru);
                    intent.putExtra("nume", getIntent().getExtras().getString("nume"));
                    intent.putExtra("sex", getIntent().getExtras().getString("sex"));
                    intent.putExtra("email", getIntent().getExtras().getString("email"));
                    intent.putExtra("telefon", getIntent().getExtras().getString("telefon"));
                    intent.putExtra("grupa", getIntent().getExtras().getString("grupa"));
                    intent.putExtra("judet", judetIntent);
                    intent.putExtra("dateDonare", getIntent().getExtras().getString("dataDonare"));
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditBookingActivity.this, ActualBookActivity.class));
                slotPosition = null;
                finish();
            }
        });

        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataDialog();
            }
        });
    }

    private void showDataDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 60);

        int Year, Month, Day, Hour, Minute;
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = DatePickerDialog.newInstance(EditBookingActivity.this, Year, Month, Day);

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
                                .document(centru)
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
                                        List<TimesEditSlot> timeSlots = new ArrayList<>();
                                        for(QueryDocumentSnapshot documentSnapshot1:task.getResult())
                                            timeSlots.add(documentSnapshot1.toObject(TimesEditSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots, Long.valueOf(Integer.parseInt(slotIntent)));
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
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(mMessageReceiver);
        localBroadcastFlag.unregisterReceiver(mFlagReceiver);
        super.onDestroy();
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimesEditSlot> timeSlotList, Long slot) {
        MyTimeEditSlotAdapter adapter = new MyTimeEditSlotAdapter(EditBookingActivity.this, timeSlotList, Long.valueOf(Integer.parseInt(slotIntent)));
        recyclerViewSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(EditBookingActivity.this, message, Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeEditSlotAdapter adapter = new MyTimeEditSlotAdapter(EditBookingActivity.this);
        recyclerViewSlot.setAdapter(adapter);

        dialog.dismiss();
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
            day = simpleDateFormat2.format(calendar.getTime());
            mDate.setText(day);
            Common.currentDate = calendar;
            loadAvailableTimeSlot(centru, simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditBookingActivity.this, ActualBookActivity.class));
        slotPosition = null;
        finish();
    }
}
