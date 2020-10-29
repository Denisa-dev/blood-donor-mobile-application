package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

public class BonusActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, tvtz, tvts, tvtv, tvtl;
    TextView tz, tnoz, ts, tns, tv, tnv, tvv, tvnv;
    static Date noDates;
    private static Integer numberOfDates, lifes, bons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = (TextView) toolbar.findViewById(R.id.tvsetBar);
        textView.setText("Bonus");

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        t1 = findViewById(R.id.tvlifes);
        t2 = findViewById(R.id.tvdays);
        t3 = findViewById(R.id.tvset);
        t4 = findViewById(R.id.tvbon);
        t5 = findViewById(R.id.tvmine);
        tz = findViewById(R.id.tv_zile);
        tnoz = findViewById(R.id.tv_no_zile);
        ts = findViewById(R.id.tv_set);
        tns = findViewById(R.id.tv_no_set);
        tv = findViewById(R.id.tv_val);
        tnv = findViewById(R.id.tv_no_val);
        tvtz = findViewById(R.id.tv_tot_z);
        tvts = findViewById(R.id.tv_tot_set);
        tvtv = findViewById(R.id.tv_tot_val);
        tvtl = findViewById(R.id.tv_vieti_tot);
        tvv = findViewById(R.id.tv_v);
        tvnv = findViewById(R.id.tv_nov);

        t5.setText(getResources().getText(R.string.myBenefits));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BonusActivity.this, HomeActivity.class));
                finish();
            }
        });

        CollectionReference bonRef = FirebaseFirestore.getInstance().collection("userAccess")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Dates");

        bonRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                    return;
                numberOfDates = queryDocumentSnapshots.size();
                lifes = 3 * numberOfDates;
                bons = 60 * numberOfDates;
                t1.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                t2.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                t3.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                t4.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

                tvtz.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                tvts.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                tvtv.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                tvtl.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

                t1.setText(lifes.toString());
                t2.setText(numberOfDates.toString());
                t3.setText(numberOfDates.toString());
                t4.setText(bons.toString());
            }
        });

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(Integer.parseInt(task.getResult().get("noBookings").toString()) > 0)
                        noDates = task.getResult().getDate("dateBooking");
                    else
                        noDates = task.getResult().getDate("dataDonare");
                    if (noDates.after(Calendar.getInstance().getTime())
                            || Common.simpleFormatDate.format(noDates).equals(Common.simpleFormatDate.format(Calendar.getInstance().getTime()))) {

                        tz.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        ts.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tv.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tnoz.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tns.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tnv.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tvv.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        tvnv.setTextColor(getResources().getColor(android.R.color.holo_green_light));

                        tnoz.setText("1");
                        tns.setText("1");
                        tnv.setText("60");
                        tvnv.setText("3");

                    } else {

                        tz.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        ts.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tnoz.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tns.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tnv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tvv.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        tvnv.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                        tnoz.setText("0");
                        tns.setText("0");
                        tnv.setText("0");
                        tvnv.setText("0");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
