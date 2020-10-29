package com.example.firestoredatabase.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.MyTimeSlotAdapter;
import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.Interfaces.ITimeSlotLoadListener;
import com.example.firestoredatabase.Model.TimeSlot;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.SpacesItemDecoration;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

//import android.app.DatePickerDialog;

public class Step2Fragment extends Fragment implements ITimeSlotLoadListener, DatePickerDialog.OnDateSetListener {

    DocumentReference centreRef;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;

    DatePickerDialog datePickerDialog;
    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_slot)
    RecyclerView recyclerViewSlot;
    SimpleDateFormat simpleDateFormat;
    @BindView(R.id.cardDate)
    CardView mCard;
    @BindView(R.id.tvDate)
    TextView mDate;
    public static String day;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 0);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 2);
                Common.currentDate = calendar;
                loadAvailableTimeSlot(Common.currentJudet.getJudetId(),
                        simpleDateFormat.format(calendar.getTime()));
            }
            else if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY
                    || dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY){
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Common.currentDate = calendar;
                loadAvailableTimeSlot(Common.currentJudet.getJudetId(),
                        simpleDateFormat.format(calendar.getTime()));
            }
            else if(dayOfWeek == Calendar.FRIDAY)
            {
                calendar.add(Calendar.DAY_OF_MONTH, 3);
                Common.currentDate = calendar;
                loadAvailableTimeSlot(Common.currentJudet.getJudetId(),
                        simpleDateFormat.format(calendar.getTime()));
            }
        }
    };

    private void loadAvailableTimeSlot(String centreId, final String format) {
        dialog.show();

        //centreId = "\"WC5gQnQFyIHHBJu1aM5b\"";
         ///donationLocation/Bucuresti/NewBranch/WC5gQnQFyIHHBJu1aM5b
        if(!TextUtils.isEmpty(Common.judet)) {
            centreRef = FirebaseFirestore.getInstance()
                    .collection("donationLocation")
                    .document(Common.judet)
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
                                .document(Common.judet)
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
                }
            }
        });
    }


    static Step2Fragment instance;

    public static Step2Fragment getInstance() {
        if(instance == null)
        {instance = new Step2Fragment();}
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;

        localBroadcastManager =  LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        //selected_date = Calendar.getInstance();
        //selected_date.add(Calendar.DATE, 0);
    }


    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView =  inflater.inflate(R.layout.step2_fragment, container, false);

        unbinder = ButterKnife.bind(this, itemView);

        //init(mDate);
//        loadAvailableTimeSlot(Common.currentJudet.getJudetId(), simpleDateFormat.format(calendar.getTime()));
        //showDataDialog(mDate);
        recyclerViewSlot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerViewSlot.setLayoutManager(gridLayoutManager);
        recyclerViewSlot.addItemDecoration(new SpacesItemDecoration(8));

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd MMM YYYY");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SATURDAY){
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            Common.currentDate = calendar;
            mDate.setText(simpleDateFormat2.format(calendar.getTime()));
        }
        else if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY
                || dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Common.currentDate = calendar;
            mDate.setText(simpleDateFormat2.format(calendar.getTime()));
        }
        else if(dayOfWeek == Calendar.FRIDAY)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            Common.currentDate = calendar;
            mDate.setText(simpleDateFormat2.format(calendar.getTime()));
        }

       // init();
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataDialog(mDate);
            }
        });
        return itemView;
    }

    private void showDataDialog(TextView mDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 60);

        int Year, Month, Day, Hour, Minute;
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = DatePickerDialog.newInstance(Step2Fragment.getInstance(), Year, Month, Day);

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
        datePickerDialog.show(getActivity().getFragmentManager(),"Alege Data");

       /* final DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                /*for (Calendar loopdate = calendar; calendar.before(endDate); calendar.add(Calendar.DATE, 1), loopdate = calendar) {
                    int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                        Calendar[] disabledDays =  new Calendar[1];
                        disabledDays[0] = loopdate;
                        datePicker.setDisabledDays(disabledDays);
                    }
                }
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd MMM YYYY");
                day = simpleDateFormat2.format(calendar.getTime());
                mDate.setText(day);
                Common.currentDate = calendar;
                loadAvailableTimeSlot(Common.currentJudet.getJudetId(), simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePicker.getDatePicker().setMaxDate(endDate.getTimeInMillis());
        datePicker.show();*/

    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        recyclerViewSlot.setAdapter(adapter);

        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
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
        loadAvailableTimeSlot(Common.currentJudet.getJudetId(), simpleDateFormat.format(calendar.getTime()));
    }
}
