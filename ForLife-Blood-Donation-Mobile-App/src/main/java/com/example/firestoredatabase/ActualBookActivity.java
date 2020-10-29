package com.example.firestoredatabase;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActualBookActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView editName;
    private TextView editTel1, editTel2;
    private TextView editTime;
    private TextView editDate;
    private TextView noBook;
    private TextView info;
    private Integer noBookings;
    public String date;
    private LinearLayout mId;

    private static final int REQUEST_CALL = 1;
    public static Date lastDate, dataDonare;
    public static String judetAccess, centreId, firstDate, slot, lat, longi, calendarFlag, idPerson;
    public static List<String> list;
    public static Integer pos, ident = 0;
    public static String grupa, telefon, nume, sex, email;

    ImageView map, mCall1, mCall2;

    Button cancel, edit;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollRef = db.collection("users");
    private DocumentReference userRef = db.document("centru/" + mFirebaseAuth.getUid());
    private static final String TAG = "ActualBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_book);

        editName = findViewById(R.id.text_centru);
        editTel1 = findViewById(R.id.centru_telefon_text1);
        editTel2 = findViewById(R.id.centru_telefon_text2);
        editTime = findViewById(R.id.time_centru);
        editDate = findViewById(R.id.date_centru);
        noBook = findViewById(R.id.noBook);
        cancel = findViewById(R.id.btn_cancel);
        edit = findViewById(R.id.btn_edit);
        map = findViewById(R.id.map);
        mCall1 = findViewById(R.id.call1);
        mCall2 = findViewById(R.id.call2);
        mId = findViewById(R.id.linearId);
        info = findViewById(R.id.info);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActualBookActivity.this, HomeActivity.class));
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEventCalendar();
            }
        });

        Calendar calendar = Calendar.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    DocumentReference ui = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(mFirebaseAuth.getUid());
                    ui.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        noBookings = Integer.parseInt(documentSnapshot.get("noBookings").toString());
                                        if (noBookings > 0)
                                            lastDate = documentSnapshot.getDate("dateBooking");
                                        else
                                            lastDate = documentSnapshot.getDate("dataDonare");
                                        if (Common.simpleFormatDate.format(lastDate).equals(Common.simpleFormatDate.format(calendar.getTime()))) {
                                            edit.setVisibility(View.GONE);
                                            cancel.setVisibility(View.GONE);
                                            info.setVisibility(View.GONE);
                                        }
                                        dataDonare = documentSnapshot.getDate("dataDonare");
                                        grupa = documentSnapshot.getString("grupaSange");
                                        telefon = documentSnapshot.getString("telefon");
                                        nume = documentSnapshot.getString("nume");
                                        sex = documentSnapshot.getString("sex");
                                        email = documentSnapshot.getString("email");
                                        DocumentReference bookInfo = FirebaseFirestore.getInstance()
                                                .collection("userAccess")
                                                .document(mFirebaseAuth.getUid())
                                                .collection("Dates")
                                                .document(Common.simpleFormatDate.format(lastDate));

                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
                                        String day = simpleDateFormat.format(lastDate);
                                        bookInfo.get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot3) {
                                                        if (documentSnapshot3.exists()) {

                                                            DocumentReference getInfoBooking = FirebaseFirestore.getInstance()
                                                                    .collection("donationLocation")
                                                                    .document(documentSnapshot3.getString("judet"))
                                                                    .collection("NewBranch")
                                                                    .document(documentSnapshot3.getString("centreId"))
                                                                    .collection(Common.simpleFormatDate.format(lastDate))
                                                                    .document(documentSnapshot3.getString("slot"));

                                                            getInfoBooking.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot dc = task.getResult();
                                                                        if (!dc.exists()) {
                                                                            Common.getNotifyAllow = true;
                                                                            Common.bookCheck = false;
                                                                            Common.noBook -= Common.noBook;
                                                                            DocumentReference userBook = FirebaseFirestore.getInstance()
                                                                                    .collection("users")
                                                                                    .document(mFirebaseAuth.getUid());
                                                                            userBook.update("noBookings", Common.noBook,
                                                                                    "dateBooking", dataDonare);
                                                                            bookInfo.delete();
                                                                            startActivity(new Intent(ActualBookActivity.this, NoBookingActivity.class));
                                                                            finish();
                                                                            Toast.makeText(ActualBookActivity.this, "Se pare că rezervarea a fost anulată de centrul la care ați avut rezervare.", Toast.LENGTH_LONG).show();
                                                                        } else {
                                                                            DocumentReference accessBookings = FirebaseFirestore.getInstance()
                                                                                    .collection("donationLocation")
                                                                                    .document(documentSnapshot3.getString("judet"))
                                                                                    .collection("NewBranch")
                                                                                    .document(documentSnapshot3.getString("centreId"));

                                                                            accessBookings.get()
                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                                                                            //telefon = documentSnapshot1.getString("telefon");
                                                                                            list = (List<String>) documentSnapshot1.get("telefon");
                                                                                            lat = documentSnapshot1.getString("lat");
                                                                                            longi = documentSnapshot1.getString("long");
                                                                                            String line = list.get(0);
                                                                                            editTel1.setText(line);
                                                                                            if (list.size() > 1) {
                                                                                                if (list.get(1) == null) {
                                                                                                    editTel2.setText("");
                                                                                                    mCall2.setVisibility(View.INVISIBLE);
                                                                                                } else {
                                                                                                    editTel2.setText(list.get(1));
                                                                                                }
                                                                                            } else {
                                                                                                editTel2.setVisibility(View.GONE);
                                                                                                mCall2.setVisibility(View.GONE);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                            if (dc.get("identificator") != null) {
                                                                                ident = Integer.parseInt(dc.get("identificator").toString());
                                                                                idPerson = dc.get("docIDPacient").toString();
                                                                            }
                                                                            calendarFlag = dc.get("calendar").toString();
                                                                            judetAccess = documentSnapshot3.getString("judet");
                                                                            centreId = documentSnapshot3.getString("centreId");
                                                                            firstDate = documentSnapshot3.getString("date");
                                                                            slot = documentSnapshot3.getString("slot");
                                                                            editName.setText(documentSnapshot3.getString("centreId"));
                                                                            editTime.setText(documentSnapshot3.getString("time"));
                                                                            editDate.setText(day);//+ " " + month + " " + year);
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d(TAG, "Rezervarea a fost anulată de centru");
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                } /*else {
                    Toast.makeText(ActualBookActivity.this, "Nu primesc", Toast.LENGTH_SHORT).show();
                }*/
            }
        };

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActualBookActivity.this, CentruMapActivity.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActualBookActivity.this, EditBookingActivity.class);
                i.putExtra("date", Common.simpleFormatDate.format(lastDate));
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM YYYY");
                i.putExtra("dateLong", sdf2.format(lastDate));
                i.putExtra("calendar", sdf.format(lastDate));
                i.putExtra("dataDonare", sdf.format(dataDonare));
                i.putExtra("slot", slot);
                i.putExtra("nume", nume);
                i.putExtra("sex", sex);
                i.putExtra("email", email);
                i.putExtra("telefon", telefon);
                i.putExtra("grupa", grupa);
                i.putExtra("centreId", centreId);
                i.putExtra("judet", judetAccess);
                startActivity(i);
                finish();
            }
        });


        mCall1.setOnClickListener(this);
        mCall2.setOnClickListener(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActualBookActivity.this);
                builder.setTitle("Sigur vreți să ștergeți rezervarea?");
                builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Common.docIDPerson = "9DQeSLHgVzI9jf1ftLjb";
                        //Toast.makeText(ActualBookActivity.this, Common.docIDPerson, Toast.LENGTH_LONG).show();
                        if (ident == 1) {
                            if (idPerson != null || !idPerson.isEmpty()) {
                                DocumentReference updateNoBookings = FirebaseFirestore.getInstance()
                                        .collection("specialCases").document(idPerson);
                                updateNoBookings.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Integer no = Integer.parseInt(task.getResult().get("nrBookings").toString());
                                        updateNoBookings.update("nrBookings", (no - 1));
                                    }
                                });
                            }
                        }

                        //pentru user
                        DocumentReference userBook = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(mFirebaseAuth.getUid());
                        userBook.update("noBookings", (Common.noBook - 1),
                                "dateBooking", dataDonare)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(ActualBookActivity.this, HomeActivity.class));
                                    }
                                });

                        DocumentReference accessBookings = FirebaseFirestore.getInstance()
                                .collection("donationLocation")
                                .document(judetAccess)
                                .collection("NewBranch")
                                .document(centreId)
                                .collection(firstDate)
                                .document(slot);

                        accessBookings.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(ActualBookActivity.this, "Anulare reușită", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        DocumentReference deleteNoBook = FirebaseFirestore.getInstance()
                                .collection("donationLocation")
                                .document(judetAccess);

                        deleteNoBook.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (sex.equals("Feminin")) {
                                    Integer noF = Integer.parseInt(task.getResult().get("noBookingsF").toString());
                                    deleteNoBook.update("noBookingsF", (noF - 1));
                                } else if (sex.equals("Masculin")) {
                                    Integer noM = Integer.parseInt(task.getResult().get("noBookingsM").toString());
                                    deleteNoBook.update("noBookingsM", (noM - 1));
                                }
                            }
                        });

                        DocumentReference userAccess = FirebaseFirestore.getInstance()
                                .collection("userAccess")
                                .document(mFirebaseAuth.getUid())
                                .collection("Dates")
                                .document(firstDate);

                        userAccess.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(ActualBookActivity.this, "Anulare reușită", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        if (ContextCompat.checkSelfPermission(ActualBookActivity.this,
                                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                            Uri deleteUri = null;
                            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                            int rows = getContentResolver().delete(deleteUri, null, null);
                        }
                        //Toast.makeText(this, "Event deleted", Toast.LENGTH_LONG).show();
                        Toast.makeText(ActualBookActivity.this, "Anulare reușită. Puteți să faceți altă rezervare.", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(ActualBookActivity.this, NoBookingActivity.class));
                    }
                });
                builder.show();
            }
        });
    }

    public void addEventCalendar() {
        if (ContextCompat.checkSelfPermission(ActualBookActivity.this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ActualBookActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    2);
        } else {
            if (calendarFlag.equals("nu")) {
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(lastDate);
                Calendar endTime = beginTime;
                //beginTime.add(Calendar.DAY_OF_MONTH, -1);
                int year = beginTime.get(Calendar.YEAR);
                int month = beginTime.get(Calendar.MONTH);
                int day = beginTime.get(Calendar.DAY_OF_MONTH);
                //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
                beginTime.set(year, month, day - 1, 17, 30);
                endTime.set(year, month, day - 1, 17, 30);
                //Toast.makeText(getContext(), beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                ContentValues l_event = new ContentValues();
                l_event.put("calendar_id", 1);
                l_event.put("title", "ForLife Rezervare Donare de Sânge");
                l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Integer.parseInt(slot)));
                l_event.put("eventLocation", centreId);
                l_event.put("dtstart", beginTime.getTimeInMillis());
                l_event.put("dtend", endTime.getTimeInMillis());
                l_event.put("hasAlarm", 1);
                l_event.put("allDay", 0);

                l_event.put("eventTimezone", "Romania");
                Uri l_eventUri;
                if (Build.VERSION.SDK_INT >= 8) {
                    l_eventUri = Uri.parse("content://com.android.calendar/events");
                    Toast.makeText(ActualBookActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
                } else {
                    l_eventUri = Uri.parse("content://calendar/events");
                    Toast.makeText(ActualBookActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                }
                Uri l_uri = ActualBookActivity.this.getContentResolver()
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
                    Uri r_uri = ActualBookActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                    if (r_uri != null) {
                        long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                    }

                    DocumentReference getInfoBooking = FirebaseFirestore.getInstance()
                            .collection("donationLocation")
                            .document(judetAccess)
                            .collection("NewBranch")
                            .document(centreId)
                            .collection(Common.simpleFormatDate.format(lastDate))
                            .document(slot);
                    getInfoBooking.update("calendar", "da")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    calendarFlag = "da";
                                }
                            });
                }
            } else {
                Toast.makeText(ActualBookActivity.this, "Evenimentul este deja adăugat. Verificați calendarul. :)", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void makePhoneCall(String phone) {
        String point = ".";
        String trimString = phone.replace(point, "");
        Toast.makeText(ActualBookActivity.this, trimString, Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(ActualBookActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActualBookActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + trimString;
            startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pos == 1)
                    makePhoneCall(editTel1.getText().toString());
                else
                    makePhoneCall(editTel2.getText().toString());
            } else {
                Toast.makeText(ActualBookActivity.this, "Nu ați permis accesul pentru a efectua apelul.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (calendarFlag.equals("nu")) {
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.setTime(lastDate);
                    Calendar endTime = beginTime;
                    //beginTime.add(Calendar.DAY_OF_MONTH, -1);
                    int year = beginTime.get(Calendar.YEAR);
                    int month = beginTime.get(Calendar.MONTH);
                    int day = beginTime.get(Calendar.DAY_OF_MONTH);
                    //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
                    beginTime.set(year, month, day - 1, 17, 30);
                    endTime.set(year, month, day - 1, 17, 30);
                    //Toast.makeText(getContext(), beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                    ContentValues l_event = new ContentValues();
                    l_event.put("calendar_id", 1);
                    l_event.put("title", "ForLife Rezervare Donare de Sânge");
                    l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Integer.parseInt(slot)));
                    l_event.put("eventLocation", centreId);
                    l_event.put("dtstart", beginTime.getTimeInMillis());
                    l_event.put("dtend", endTime.getTimeInMillis());
                    l_event.put("hasAlarm", 1);
                    l_event.put("allDay", 0);

                    l_event.put("eventTimezone", "Romania");
                    Uri l_eventUri;
                    if (Build.VERSION.SDK_INT >= 8) {
                        l_eventUri = Uri.parse("content://com.android.calendar/events");
                        Toast.makeText(ActualBookActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    } else {
                        l_eventUri = Uri.parse("content://calendar/events");
                        Toast.makeText(ActualBookActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    }
                    Uri l_uri = ActualBookActivity.this.getContentResolver()
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
                        Uri r_uri = ActualBookActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                        if (r_uri != null) {
                            long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                        }
                    }

                    DocumentReference getInfoBooking = FirebaseFirestore.getInstance()
                            .collection("donationLocation")
                            .document(judetAccess)
                            .collection("NewBranch")
                            .document(centreId)
                            .collection(Common.simpleFormatDate.format(lastDate))
                            .document(slot);
                    getInfoBooking.update("calendar", "da")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    calendarFlag = "da";
                                }
                            });
                } else {
                    Toast.makeText(ActualBookActivity.this, "Evenimentul este deja adăugat. Verificați calendarul. :)", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(ActualBookActivity.this, "Nu s-a permis accesul pentru calendar!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call1:
                pos = 1;
                makePhoneCall(editTel1.getText().toString());
                break;
            case R.id.call2:
                pos = 2;
                makePhoneCall(editTel2.getText().toString());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActualBookActivity.this, HomeActivity.class));
        finish();
    }
}
