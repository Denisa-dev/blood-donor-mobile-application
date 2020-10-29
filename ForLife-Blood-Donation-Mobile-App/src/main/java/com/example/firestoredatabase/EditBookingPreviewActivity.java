package com.example.firestoredatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditBookingPreviewActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button confirm, cancel;
    TextView centru, ora, data;
    Integer slotNew;
    public static ReservationInfo reservationInfo = new ReservationInfo();

    public static String nume, sex, telefon, email, grupa;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking_preview);

        confirm = findViewById(R.id.btn_confirmBooking);
        centru = findViewById(R.id.txtCentreName);
        ora = findViewById(R.id.time_centru);
        data = findViewById(R.id.txtBookingTime);
        cancel = findViewById(R.id.btn_cancel);
        slotNew = getIntent().getExtras().getInt("slotNew");

        centru.setText(getIntent().getExtras().getString("centreId"));
        ora.setText(Common.converTimeSlotToString(getIntent().getExtras().getInt("slotNew")));
        data.setText(simpleDateFormat.format(Common.currentDate.getTime()));
        Common.editDateDonation = Common.currentDate;
        nume = getIntent().getExtras().getString("nume");
        sex = getIntent().getExtras().getString("sex");
        email = getIntent().getExtras().getString("email");
        telefon = getIntent().getExtras().getString("telefon");
        grupa = getIntent().getExtras().getString("grupa");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            cal.setTime(sdf.parse(getIntent().getExtras().getString("dateDonare")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Toast.makeText(EditBookingPreviewActivity.this, cal.getTime().toString(), Toast.LENGTH_SHORT).show();

        //pentru centru
        reservationInfo.setTelefon(telefon);
        reservationInfo.setSex(sex);
        reservationInfo.setName(nume);
        reservationInfo.setGrupa(grupa);
        reservationInfo.setEmail(email);
        reservationInfo.setCentreId(getIntent().getExtras().getString("centreId"));
        reservationInfo.setCustomeName(getIntent().getExtras().getString("centreId"));
        reservationInfo.setTime(Common.converTimeSlotToString(getIntent().getExtras().getInt("slotNew")));
        reservationInfo.setDate(Common.editDateDonation.getTime().toString());
        reservationInfo.setSlot(Long.valueOf(getIntent().getExtras().getInt("slotNew")));

        UserInfo userInfo = new UserInfo();
        userInfo.setCentreId(getIntent().getExtras().getString("centreId"));
        userInfo.setDate(Common.simpleFormatDate.format(Common.editDateDonation.getTime()));
        userInfo.setData(Common.editDateDonation.getTime());
        userInfo.setJudet(getIntent().getExtras().getString("judet"));
        userInfo.setSlot(String.valueOf(getIntent().getExtras().getInt("slotNew")));
        userInfo.setTime(Common.converTimeSlotToString(getIntent().getExtras().getInt("slotNew")));


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                c.setTime(Common.currentDate.getTime());
                //c.add(Calendar.MONTH, -3);
                Calendar c2 = Calendar.getInstance();
                c2.setTime(Common.actualDate.getTime());
                //c2.add(Calendar.MONTH, -3);

                if (c.getTime().before(c2.getTime())) {
                    c.add(Calendar.MONTH, -3);
                    //Toast.makeText(EditBookingPreviewActivity.this, c.getTime().toString(), Toast.LENGTH_SHORT).show();
                    if (c.getTime().before(cal.getTime())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditBookingPreviewActivity.this);
                        builder.setTitle("Ne pare rău, dar nu puteți face actualizarea în această dată!");
                        builder.setMessage("Încă nu au trecut minim trei luni de la ultima donare. Mulțumim pentru înțelegere!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                    } else if (Common.simpleFormatDate.format(c.getTime()).equals(Common.simpleFormatDate.format(cal.getTime())) || c.getTime().after(cal.getTime())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditBookingPreviewActivity.this);
                        builder.setTitle("Adăugare rezervare în calendar");
                        builder.setMessage("Doriți să vă adăugăm memento în calendar pentru rezervare?");
                        builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(EditBookingPreviewActivity.this,
                                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                    Uri deleteUri = null;
                                    deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                                    int rows = getContentResolver().delete(deleteUri, null, null);
                                }
                                reservationInfo.setCalendar("da");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
                                Common.getNotifyAllow = true;
                                DocumentReference deleteBookings = FirebaseFirestore.getInstance()
                                        .collection("donationLocation")
                                        .document(getIntent().getExtras().getString("judet"))
                                        .collection("NewBranch")
                                        .document(getIntent().getExtras().getString("centreId"))
                                        .collection(sdf.format(Common.actualDate.getTime()))
                                        .document(getIntent().getExtras().getString("slotOld"));

                                deleteBookings.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentReference updateDateLast = FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(mFirebaseAuth.getUid());

                                                updateDateLast.update("dateBooking", Common.currentDate.getTime());

                                                DocumentReference deleteUserAccess = FirebaseFirestore.getInstance()
                                                        .collection("userAccess")
                                                        .document(mFirebaseAuth.getUid())
                                                        .collection("Dates")
                                                        .document(sdf.format(Common.actualDate.getTime()));

                                                deleteUserAccess.delete();

                                                DocumentReference updateDate = FirebaseFirestore.getInstance()
                                                        .collection("donationLocation")
                                                        .document(getIntent().getExtras().getString("judet"))
                                                        .collection("NewBranch")
                                                        .document(getIntent().getExtras().getString("centreId"))
                                                        .collection(sdf.format(Common.editDateDonation.getTime()))
                                                        .document(String.valueOf(getIntent().getExtras().getInt("slotNew"))); //book date

                                                updateDate.set(reservationInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(EditBookingPreviewActivity.this, "Rezervare actualizată cu succes!", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                        .collection("userAccess")
                                                        .document(mFirebaseAuth.getUid())
                                                        .collection("Dates")
                                                        .document(Common.simpleFormatDate.format(Common.editDateDonation.getTime()));
                                                userInfoBook.set(userInfo);
                                                addEventCalendar();
                                            }
                                        });
                            }
                        });

                        builder.setNegativeButton("NU", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(EditBookingPreviewActivity.this,
                                        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                    Uri deleteUri = null;
                                    deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                                    int rows = getContentResolver().delete(deleteUri, null, null);
                                }
                                reservationInfo.setCalendar("nu");
                                SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
                                Common.getNotifyAllow = true;
                                DocumentReference deleteBookings = FirebaseFirestore.getInstance()
                                        .collection("donationLocation")
                                        .document(getIntent().getExtras().getString("judet"))
                                        .collection("NewBranch")
                                        .document(getIntent().getExtras().getString("centreId"))
                                        .collection(sdf.format(Common.actualDate.getTime()))
                                        .document(getIntent().getExtras().getString("slotOld"));

                                deleteBookings.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentReference updateDateLast = FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(mFirebaseAuth.getUid());

                                                updateDateLast.update("dateBooking", Common.currentDate.getTime());

                                                DocumentReference deleteUserAccess = FirebaseFirestore.getInstance()
                                                        .collection("userAccess")
                                                        .document(mFirebaseAuth.getUid())
                                                        .collection("Dates")
                                                        .document(sdf.format(Common.actualDate.getTime()));

                                                deleteUserAccess.delete();

                                                DocumentReference updateDate = FirebaseFirestore.getInstance()
                                                        .collection("donationLocation")
                                                        .document(getIntent().getExtras().getString("judet"))
                                                        .collection("NewBranch")
                                                        .document(getIntent().getExtras().getString("centreId"))
                                                        .collection(sdf.format(Common.editDateDonation.getTime()))
                                                        .document(String.valueOf(getIntent().getExtras().getInt("slotNew"))); //book date

                                                updateDate.set(reservationInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(EditBookingPreviewActivity.this, "Rezervare actualizată cu succes!", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                        .collection("userAccess")
                                                        .document(mFirebaseAuth.getUid())
                                                        .collection("Dates")
                                                        .document(Common.simpleFormatDate.format(Common.editDateDonation.getTime()));
                                                userInfoBook.set(userInfo);
                                                startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
                                                finish();
                                            }
                                        });
                            }
                        });

                        builder.show();
                    }
                } else if (c.getTime().after(c2.getTime()) || Common.simpleFormatDate.format(c.getTime()).equals(Common.simpleFormatDate.format(c2.getTime()))) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(EditBookingPreviewActivity.this);
                    builder.setTitle("Adăugare rezervare în calendar");
                    builder.setMessage("Doriți să vă adăugăm reminder în calendar pentru rezervare?");
                    builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ContextCompat.checkSelfPermission(EditBookingPreviewActivity.this,
                                    Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                Uri deleteUri = null;
                                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                                int rows = getContentResolver().delete(deleteUri, null, null);
                            }
                            reservationInfo.setCalendar("da");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
                            Common.getNotifyAllow = true;
                            DocumentReference deleteBookings = FirebaseFirestore.getInstance()
                                    .collection("donationLocation")
                                    .document(getIntent().getExtras().getString("judet"))
                                    .collection("NewBranch")
                                    .document(getIntent().getExtras().getString("centreId"))
                                    .collection(sdf.format(Common.actualDate.getTime()))
                                    .document(getIntent().getExtras().getString("slotOld"));

                            deleteBookings.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DocumentReference updateDateLast = FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(mFirebaseAuth.getUid());

                                            updateDateLast.update("dateBooking", Common.currentDate.getTime());

                                            DocumentReference deleteUserAccess = FirebaseFirestore.getInstance()
                                                    .collection("userAccess")
                                                    .document(mFirebaseAuth.getUid())
                                                    .collection("Dates")
                                                    .document(sdf.format(Common.actualDate.getTime()));

                                            deleteUserAccess.delete();

                                            DocumentReference updateDate = FirebaseFirestore.getInstance()
                                                    .collection("donationLocation")
                                                    .document(getIntent().getExtras().getString("judet"))
                                                    .collection("NewBranch")
                                                    .document(getIntent().getExtras().getString("centreId"))
                                                    .collection(sdf.format(Common.editDateDonation.getTime()))
                                                    .document(String.valueOf(getIntent().getExtras().getInt("slotNew"))); //book date

                                            updateDate.set(reservationInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(EditBookingPreviewActivity.this, "Rezervare actualizată cu succes!", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                    .collection("userAccess")
                                                    .document(mFirebaseAuth.getUid())
                                                    .collection("Dates")
                                                    .document(Common.simpleFormatDate.format(Common.editDateDonation.getTime()));
                                            userInfoBook.set(userInfo);
                                            addEventCalendar();
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton("NU", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ContextCompat.checkSelfPermission(EditBookingPreviewActivity.this,
                                    Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                Uri deleteUri = null;
                                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                                int rows = getContentResolver().delete(deleteUri, null, null);
                            }
                            reservationInfo.setCalendar("nu");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
                            Common.getNotifyAllow = true;
                            DocumentReference deleteBookings = FirebaseFirestore.getInstance()
                                    .collection("donationLocation")
                                    .document(getIntent().getExtras().getString("judet"))
                                    .collection("NewBranch")
                                    .document(getIntent().getExtras().getString("centreId"))
                                    .collection(sdf.format(Common.actualDate.getTime()))
                                    .document(getIntent().getExtras().getString("slotOld"));

                            deleteBookings.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DocumentReference updateDateLast = FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(mFirebaseAuth.getUid());

                                            updateDateLast.update("dateBooking", Common.currentDate.getTime());

                                            DocumentReference deleteUserAccess = FirebaseFirestore.getInstance()
                                                    .collection("userAccess")
                                                    .document(mFirebaseAuth.getUid())
                                                    .collection("Dates")
                                                    .document(sdf.format(Common.actualDate.getTime()));

                                            deleteUserAccess.delete();

                                            DocumentReference updateDate = FirebaseFirestore.getInstance()
                                                    .collection("donationLocation")
                                                    .document(getIntent().getExtras().getString("judet"))
                                                    .collection("NewBranch")
                                                    .document(getIntent().getExtras().getString("centreId"))
                                                    .collection(sdf.format(Common.editDateDonation.getTime()))
                                                    .document(String.valueOf(getIntent().getExtras().getInt("slotNew"))); //book date

                                            updateDate.set(reservationInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(EditBookingPreviewActivity.this, "Rezervare actualizată cu succes!", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                    .collection("userAccess")
                                                    .document(mFirebaseAuth.getUid())
                                                    .collection("Dates")
                                                    .document(Common.simpleFormatDate.format(Common.editDateDonation.getTime()));
                                            userInfoBook.set(userInfo);
                                            startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
                                            finish();
                                        }
                                    });
                        }
                    });

                    builder.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
                finish();
            }
        });
    }

    public void addEventCalendar() {
        if (ContextCompat.checkSelfPermission(EditBookingPreviewActivity.this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditBookingPreviewActivity.this,
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
            //Toast.makeText(EditBookingPreviewActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
            ContentValues l_event = new ContentValues();
            l_event.put("calendar_id", 1);
            l_event.put("title", "ForLife - Rezervare Donare de Sânge");
            l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(getIntent().getExtras().getInt("slotNew")));
            l_event.put("eventLocation", getIntent().getExtras().getString("judet"));
            l_event.put("dtstart", beginTime.getTimeInMillis());
            l_event.put("dtend", endTime.getTimeInMillis());
            l_event.put("hasAlarm", 1);
            l_event.put("allDay", 0);

            l_event.put("eventTimezone", "Romania");
            Uri l_eventUri;
            if (Build.VERSION.SDK_INT >= 8) {
                l_eventUri = Uri.parse("content://com.android.calendar/events");
                Toast.makeText(EditBookingPreviewActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
            } else {
                l_eventUri = Uri.parse("content://calendar/events");
                Toast.makeText(EditBookingPreviewActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
            }
            Uri l_uri = EditBookingPreviewActivity.this.getContentResolver()
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
                Uri r_uri = EditBookingPreviewActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                if (r_uri != null) {
                    long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                }
            }
            startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
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
                    //Toast.makeText(EditBookingPreviewActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                    ContentValues l_event = new ContentValues();
                    l_event.put("calendar_id", 1);
                    l_event.put("title", "ForLife - Rezervare Donare de Sânge");
                    l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(getIntent().getExtras().getInt("slotNew")));
                    l_event.put("eventLocation", getIntent().getExtras().getString("judet"));
                    l_event.put("dtstart", beginTime.getTimeInMillis());
                    l_event.put("dtend", endTime.getTimeInMillis());
                    l_event.put("hasAlarm", 1);
                    l_event.put("allDay", 0);

                    l_event.put("eventTimezone", "Romania");
                    Uri l_eventUri;
                    if (Build.VERSION.SDK_INT >= 8) {
                        l_eventUri = Uri.parse("content://com.android.calendar/events");
                        Toast.makeText(EditBookingPreviewActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    } else {
                        l_eventUri = Uri.parse("content://calendar/events");
                        Toast.makeText(EditBookingPreviewActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    }
                    Uri l_uri = EditBookingPreviewActivity.this.getContentResolver()
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
                        Uri r_uri = EditBookingPreviewActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                        if (r_uri != null) {
                            long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                        }
                    }
                    startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
                    finish();
                } else {
                    Toast.makeText(EditBookingPreviewActivity.this, "Nu s-a permis accesul pentru calendar!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
                    finish();
                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditBookingPreviewActivity.this, ActualBookActivity.class));
        finish();
    }
}
