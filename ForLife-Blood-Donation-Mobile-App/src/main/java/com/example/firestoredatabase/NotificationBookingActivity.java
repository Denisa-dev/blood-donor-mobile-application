package com.example.firestoredatabase;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.firestoredatabase.Model.ReservationInfo;
import com.example.firestoredatabase.Model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationBookingActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef = db.collection("users").document(mFirebaseAuth.getUid());

    Button confirm, cancel;
    TextView centru, ora, data;
    private static Integer slotNew, noBooking, greutate, varsta;
    private static Date lastDate, dataDonare;
    private static ReservationInfo reservationInfo = new ReservationInfo();

    public static String centruIntent, judetIntent, nume, sex, telefon, grupa, email;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_YYYY");
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_booking);

        centruIntent = getIntent().getExtras().getString("centreId");
        judetIntent = getIntent().getExtras().getString("judet");
        slotNew = getIntent().getExtras().getInt("slotNew");

        confirm = findViewById(R.id.btn_confirmBooking);
        centru = findViewById(R.id.txtCentreName);
        ora = findViewById(R.id.time_centru);
        data = findViewById(R.id.txtBookingTime);
        cancel = findViewById(R.id.btn_cancel);

        centru.setText(centruIntent);
        ora.setText(Common.converTimeSlotToString(slotNew));
        data.setText(sdf.format(Common.currentDate.getTime()));


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.tab = 1;
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                c.setTime(Common.currentDate.getTime());
                Calendar c2 = Calendar.getInstance();
                c2.setTime(Common.currentDate.getTime());

                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            nume = documentSnapshot.getString("nume");
                            sex = documentSnapshot.getString("sex");
                            telefon = documentSnapshot.getString("telefon");
                            grupa = documentSnapshot.getString("grupaSange");
                            email = mFirebaseAuth.getCurrentUser().getEmail();
                            noBooking = Integer.parseInt(documentSnapshot.get("noBookings").toString());
                            greutate = Integer.parseInt(documentSnapshot.get("greutate").toString());
                            varsta = Integer.parseInt(documentSnapshot.get("varsta").toString());
                            if (noBooking > 0) {
                                lastDate = documentSnapshot.getDate("dateBooking");
                                dataDonare = documentSnapshot.getDate("dataDonare");
                            } else {
                                lastDate = documentSnapshot.getDate("dataDonare");
                                dataDonare = documentSnapshot.getDate("dataDonare");
                            }
                            if (varsta < 18 || varsta > 60 || greutate < 50) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationBookingActivity.this);
                                builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
                                builder.setMessage("Nu îndepliniți criteriile de vârstă și greutate.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();
                            } else {
                                if (c.getTime().before(lastDate)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(NotificationBookingActivity.this);
                                    builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
                                    builder.setMessage("Există deja o rezervare activă! Vă rugăm editați-o sau ștergeți-o pe aceasta înainte!");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    builder.show();
                                } else if (c2.getTime().after(lastDate)) {
                                    if (Common.simpleFormatDate.format(c2.getTime()).equals(Common.simpleFormatDate.format(lastDate))) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationBookingActivity.this);
                                        builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
                                        builder.setMessage("Există deja o rezervare activă în ziua aceasta! Vă rugăm editați-o sau ștergeți-o pe aceasta înainte!");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        builder.show();
                                    } else {
                                        c2.add(Calendar.MONTH, -3);
                                        if (Common.simpleFormatDate.format(c2.getTime()).equals(Common.simpleFormatDate.format(lastDate)) || c2.getTime().after(lastDate)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(NotificationBookingActivity.this);
                                            builder.setTitle("Adăugare rezervare în calendar");
                                            builder.setMessage("Doriți să vă adăugăm reminder în calendar pentru rezervare?");
                                            builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    addBooking();
                                                }
                                            });
                                            builder.setNegativeButton("NU", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Common.getNotifyAllow = true;
                                                    //pentru centru
                                                    reservationInfo.setTelefon(telefon);
                                                    reservationInfo.setSex(sex);
                                                    reservationInfo.setName(nume);
                                                    reservationInfo.setGrupa(grupa);
                                                    reservationInfo.setEmail(email);
                                                    reservationInfo.setCentreId(centruIntent);
                                                    reservationInfo.setCustomeName(centruIntent);
                                                    reservationInfo.setTime(Common.converTimeSlotToString(slotNew));
                                                    reservationInfo.setDate(Common.currentDate.getTime().toString());
                                                    reservationInfo.setSlot(Long.valueOf(slotNew));
                                                    reservationInfo.setCalendar("nu");

                                                    UserInfo userInfo = new UserInfo();
                                                    userInfo.setCentreId(centruIntent);
                                                    userInfo.setDate(Common.simpleFormatDate.format(Common.currentDate.getTime()));
                                                    userInfo.setData(Common.currentDate.getTime());
                                                    userInfo.setJudet(judetIntent);
                                                    userInfo.setSlot(String.valueOf(slotNew));
                                                    userInfo.setTime(Common.converTimeSlotToString(slotNew));

                                                    DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                                            .collection("donationLocation")
                                                            .document(judetIntent)
                                                            .collection("NewBranch")
                                                            .document(centruIntent)
                                                            .collection(Common.simpleFormatDate.format(Common.currentDate.getTime()))
                                                            .document(String.valueOf(slotNew)); //book date


                                                    bookingDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                if (documentSnapshot.exists()) {
                                                                    Toast.makeText(NotificationBookingActivity.this, "Există o rezervare în acest interval!", Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    bookingDate.set(reservationInfo)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(NotificationBookingActivity.this, "Rezervare realizată cu succes! Vă mulțumim!", Toast.LENGTH_LONG).show();
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(NotificationBookingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    });

                                                                    //pentru user
                                                                    DocumentReference userBook = FirebaseFirestore.getInstance()
                                                                            .collection("users")
                                                                            .document(mFirebaseAuth.getUid());
                                                                    userBook.update("noBookings", ++Common.noBook,
                                                                            "dataDonare", lastDate,
                                                                            "dateBooking", Common.currentDate.getTime())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                }
                                                                            });

                                                                    DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                                            .collection("userAccess")
                                                                            .document(mFirebaseAuth.getUid())
                                                                            .collection("Dates")
                                                                            .document(Common.simpleFormatDate.format(Common.currentDate.getTime()));
                                                                    userInfoBook.set(userInfo);

                                                                    DocumentReference sexInfo = FirebaseFirestore.getInstance()
                                                                            .collection("donationLocation")
                                                                            .document(judetIntent);
                                                                    sexInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (sex.equals("Feminin")) {
                                                                                Integer noF = Integer.parseInt(task.getResult().get("noBookingsF").toString());
                                                                                sexInfo.update("noBookingsF", (noF + 1));
                                                                            } else if (sex.equals("Masculin")) {
                                                                                Integer noM = Integer.parseInt(task.getResult().get("noBookingsM").toString());
                                                                                sexInfo.update("noBookingsM", (noM + 1));
                                                                            }
                                                                        }
                                                                    });

                                                                    Common.currentDate.add(Calendar.DATE, 0);
                                                                    Common.noBook = 0;
                                                                    finish();
                                                                }
                                                            } else {
                                                                Toast.makeText(NotificationBookingActivity.this, "Nu s-a putut face rezervarea. Încercați din nou mai târziu. :)", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            builder.show();
                                        } else if (c2.getTime().before(lastDate)) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(NotificationBookingActivity.this);
                                            builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
                                            builder.setMessage("Încă nu au trecut minim trei luni de la ultima donare. Mulțumim pentru înțelegere!");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    public void addEventCalendar() {
        if (ContextCompat.checkSelfPermission(NotificationBookingActivity.this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) NotificationBookingActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);
        } else {
            Calendar beginTime;
            beginTime = Common.currentDate;
            Calendar endTime = beginTime;
            //beginTime.add(Calendar.DAY_OF_MONTH, -1);
            int year = beginTime.get(Calendar.YEAR);
            int month = beginTime.get(Calendar.MONTH);
            int day = beginTime.get(Calendar.DAY_OF_MONTH);
            //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
            beginTime.set(year, month, day - 1, 17, 30);
            endTime.set(year, month, day - 1, 17, 30);
            Toast.makeText(NotificationBookingActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
            ContentValues l_event = new ContentValues();
            l_event.put("calendar_id", 1);
            l_event.put("title", "ForLife - Rezervare Donare de Sânge");
            l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(slotNew));
            l_event.put("eventLocation", judetIntent);
            l_event.put("dtstart", beginTime.getTimeInMillis());
            l_event.put("dtend", endTime.getTimeInMillis());
            l_event.put("hasAlarm", 1);
            l_event.put("allDay", 0);

            l_event.put("eventTimezone", "Romania");
            Uri l_eventUri;
            if (Build.VERSION.SDK_INT >= 8) {
                l_eventUri = Uri.parse("content://com.android.calendar/events");
                Toast.makeText(NotificationBookingActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();

            } else {
                l_eventUri = Uri.parse("content://calendar/events");
                Toast.makeText(NotificationBookingActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
            }
            Uri l_uri = NotificationBookingActivity.this.getContentResolver()
                    .insert(l_eventUri, l_event);
            if (l_uri != null) {
                long eventID = Long.parseLong(l_uri.getLastPathSegment());
                Common.eventID = eventID;
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("method", 1);// will alert the user with a reminder notification
                reminderValues.put("minutes", 0);// number of minutes before the start time of the event to fire a reminder

                Uri reminder_eventUri;
                if (Build.VERSION.SDK_INT >= 8) {
                    reminder_eventUri = Uri.parse("content://com.android.calendar/reminders");
                } else {
                    reminder_eventUri = Uri.parse("content://calendar/reminders");
                }
                Uri r_uri = NotificationBookingActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                if (r_uri != null) {
                    long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                }
            }
            Common.currentDate.add(Calendar.DATE, 0);
            Common.noBook = 0;
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Calendar beginTime;
                    beginTime = Common.currentDate;
                    Calendar endTime = beginTime;
                    //beginTime.add(Calendar.DAY_OF_MONTH, -1);
                    int year = beginTime.get(Calendar.YEAR);
                    int month = beginTime.get(Calendar.MONTH);
                    int day = beginTime.get(Calendar.DAY_OF_MONTH);
                    //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
                    beginTime.set(year, month, day - 1, 17, 30);
                    endTime.set(year, month, day - 1, 17, 30);
                    Toast.makeText(NotificationBookingActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                    ContentValues l_event = new ContentValues();
                    l_event.put("calendar_id", 1);
                    l_event.put("title", "ForLife - Rezervare Donare de Sânge");
                    l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(slotNew));
                    l_event.put("eventLocation", judetIntent);
                    l_event.put("dtstart", beginTime.getTimeInMillis());
                    l_event.put("dtend", endTime.getTimeInMillis());
                    l_event.put("hasAlarm", 1);
                    l_event.put("allDay", 0);

                    l_event.put("eventTimezone", "Romania");
                    Uri l_eventUri;
                    if (Build.VERSION.SDK_INT >= 8) {
                        l_eventUri = Uri.parse("content://com.android.calendar/events");
                        Toast.makeText(NotificationBookingActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();

                    } else {
                        l_eventUri = Uri.parse("content://calendar/events");
                        Toast.makeText(NotificationBookingActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    }
                    Uri l_uri = NotificationBookingActivity.this.getContentResolver()
                            .insert(l_eventUri, l_event);
                    if (l_uri != null) {
                        long eventID = Long.parseLong(l_uri.getLastPathSegment());
                        Common.eventID = eventID;
                        ContentValues reminderValues = new ContentValues();
                        reminderValues.put("event_id", eventID);
                        reminderValues.put("method", 1);// will alert the user with a reminder notification
                        reminderValues.put("minutes", 0);// number of minutes before the start time of the event to fire a reminder

                        Uri reminder_eventUri;
                        if (Build.VERSION.SDK_INT >= 8) {
                            reminder_eventUri = Uri.parse("content://com.android.calendar/reminders");
                        } else {
                            reminder_eventUri = Uri.parse("content://calendar/reminders");
                        }
                        Uri r_uri = NotificationBookingActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                        if (r_uri != null) {
                            long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                        }
                    }
                } else {

                    Toast.makeText(NotificationBookingActivity.this, "Nu s-a permis accesul pentru calendar!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                Common.currentDate.add(Calendar.DATE, 0);
                Common.noBook = 0;
                finish();
                return;
            }

        }
    }

    public void addBooking(){
        Common.getNotifyAllow = true;
        //pentru centru
        reservationInfo.setTelefon(telefon);
        reservationInfo.setSex(sex);
        reservationInfo.setName(nume);
        reservationInfo.setGrupa(grupa);
        reservationInfo.setEmail(email);
        reservationInfo.setCentreId(centruIntent);
        reservationInfo.setCustomeName(centruIntent);
        reservationInfo.setTime(Common.converTimeSlotToString(slotNew));
        reservationInfo.setDate(Common.currentDate.getTime().toString());
        reservationInfo.setSlot(Long.valueOf(slotNew));
        reservationInfo.setCalendar("da");

        UserInfo userInfo = new UserInfo();
        userInfo.setCentreId(centruIntent);
        userInfo.setDate(Common.simpleFormatDate.format(Common.currentDate.getTime()));
        userInfo.setJudet(judetIntent);
        userInfo.setData(Common.currentDate.getTime());
        userInfo.setSlot(String.valueOf(slotNew));
        userInfo.setTime(Common.converTimeSlotToString(slotNew));

        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("donationLocation")
                .document(judetIntent)
                .collection("NewBranch")
                .document(centruIntent)
                .collection(Common.simpleFormatDate.format(Common.currentDate.getTime()))
                .document(String.valueOf(slotNew)); //book date


        bookingDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Toast.makeText(NotificationBookingActivity.this, "Există o rezervare în acest interval!", Toast.LENGTH_LONG).show();
                    } else {
                        bookingDate.set(reservationInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NotificationBookingActivity.this, "Rezervare reușită! Verificați pagina Rezervare actuală.", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NotificationBookingActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        //pentru user
                        DocumentReference userBook = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(mFirebaseAuth.getUid());
                        userBook.update("noBookings", ++Common.noBook,
                                "dataDonare", lastDate,
                                "dateBooking", Common.currentDate.getTime())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });

                        DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                .collection("userAccess")
                                .document(mFirebaseAuth.getUid())
                                .collection("Dates")
                                .document(Common.simpleFormatDate.format(Common.currentDate.getTime()));
                        userInfoBook.set(userInfo);

                        DocumentReference sexInfo = FirebaseFirestore.getInstance()
                                .collection("donationLocation")
                                .document(judetIntent);
                        sexInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (sex.equals("Feminin")) {
                                    Integer noF = Integer.parseInt(task.getResult().get("noBookingsF").toString());
                                    sexInfo.update("noBookingsF", (noF + 1));
                                } else if (sex.equals("Masculin")) {
                                    Integer noM = Integer.parseInt(task.getResult().get("noBookingsM").toString());
                                    sexInfo.update("noBookingsM", (noM + 1));
                                }
                            }
                        });
                        addEventCalendar();
                    }
                } else {
                    Toast.makeText(NotificationBookingActivity.this, "Nu s-a putut face rezervarea. Încercați din nou mai târziu. :)", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
