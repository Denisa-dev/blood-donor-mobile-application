package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PopupNotificationActivity extends AppCompatActivity {

    ImageView img;
    TextView t1, t2, t3, t4;
    Button btn;
    private static String centru, titlu, desc, grupa, judet;
    private static final String TAG = "PopupNotificationActivi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_notification);
        img = findViewById(R.id.close);
        t1 = findViewById(R.id.centru);
        t2 = findViewById(R.id.titlu);
        t3 = findViewById(R.id.descriere);
        t4 = findViewById(R.id.grupa);
        btn = findViewById(R.id.dona);

        centru = getIntent().getExtras().getString("centru");
        titlu = getIntent().getExtras().getString("titlu");
        desc = getIntent().getExtras().getString("desc");
        grupa = getIntent().getExtras().getString("grupa");
        judet = getIntent().getExtras().getString("judet");

        t1.setText(centru);
        t2.setText(titlu);
        t3.setText(desc);
        t4.setText(grupa);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .6));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.tab = 1;
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference centreRef = FirebaseFirestore.getInstance()
                        .collection("donationLocation")
                        .document(judet)
                        .collection("NewBranch")
                        .document(centru);


                centreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Common.judetTab = judet;
                                Common.centruTab = centru;
                                Log.d(TAG, judet);
                                Log.d(TAG, centru);
                                Intent intent = new Intent(PopupNotificationActivity.this, LoadNotificationActivity.class);
                                intent.putExtra("centru", centru);
                                intent.putExtra("judet", judet);
                                intent.putExtra("adresa", "orice");
                                startActivity(intent);
                                finish();
                            } else {
                                CollectionReference col = FirebaseFirestore.getInstance().collection("donationLocation");
                                col.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot dc : task.getResult()) {
                                                if (dc.getId().equals(judet)) {
                                                    //Toast.makeText(PopupNotificationActivity.this, judet, Toast.LENGTH_LONG).show();
                                                    CollectionReference doc = FirebaseFirestore.getInstance().collection("donationLocation")
                                                            .document(dc.getId()).collection("NewBranch");
                                                    doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            for (DocumentSnapshot myDoc : task.getResult()) {
                                                                if (myDoc.getId().compareTo(centru) != 0) {
                                                                    //Toast.makeText(PopupNotificationActivity.this, myDoc.getId(), Toast.LENGTH_LONG).show();
                                                                    Intent intent = new Intent(PopupNotificationActivity.this, LoadNotificationActivity.class);
                                                                    intent.putExtra("centru", myDoc.getId());
                                                                    intent.putExtra("judet", dc.getId());
                                                                    intent.putExtra("adresa", "orice");
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        }
                                                    });
                                                } else {
                                                   // Intent intent = new Intent(PopupNotificationActivity.this, ReservationActivity.class);
                                                   // startActivity(intent);
                                                   // finish();
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                        }

                    }
                });
            }
        });
    }
}
