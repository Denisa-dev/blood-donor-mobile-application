package com.example.firestoredatabase.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.HomeActivity;
import com.example.firestoredatabase.Model.ReservationInfo;
import com.example.firestoredatabase.Model.UserInfo;
import com.example.firestoredatabase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Step3Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;

    @BindView(R.id.txtCentreName)
    TextView txt_centreName;
    @BindView(R.id.txtBookingTime)
    TextView txt_bookTime;
    @BindView(R.id.time_centru)
    TextView tvTime;

    private static Integer varsta, greutate, noBookings;
    private static Date dateLastDonation, dateDataDonare;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = "Step3Fragment";
    //private CollectionReference userCollRef = db.collection("users");

    private static String name, sex, email, grupa, telefon;
    Button btn_cal;

    @OnClick(R.id.btn_confirmBooking)
    void confirmReservation() {
        if (greutate < 50 || varsta < 18 || varsta > 60) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
            builder.setMessage("Nu îndepliniți criteriile de vârstă și greutate.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        } else {
            ReservationInfo reservationInfo = new ReservationInfo();

            Calendar c = Calendar.getInstance();
            c.setTime(Common.currentDate.getTime());
            //c.add(Calendar.MONTH, -3);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(Common.currentDate.getTime());
            //c2.add(Calendar.MONTH, -3);

            if (c.getTime().before(dateLastDonation)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Ne pare rău, dar nu puteți face o rezervare!");
                builder.setMessage("Există deja o rezervare activă! Vă rugăm editați-o sau ștergeți-o pe aceasta înainte!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            } else if (c2.getTime().after(dateLastDonation)) {
                if (Common.simpleFormatDate.format(c2.getTime()).equals(Common.simpleFormatDate.format(dateLastDonation))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    if (Common.simpleFormatDate.format(c2.getTime()).equals(Common.simpleFormatDate.format(dateLastDonation)) || c2.getTime().after(dateLastDonation)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Adăugare rezervare în calendar");
                        builder.setMessage("Doriți să vă adăugăm memento în calendar pentru rezervare?");
                        builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //pentru centru
                                reservationInfo.setTelefon(telefon);
                                reservationInfo.setSex(sex);
                                reservationInfo.setName(name);
                                reservationInfo.setGrupa(grupa);
                                reservationInfo.setEmail(email);
                                reservationInfo.setCentreId(Common.currentJudet.getJudetId());
                                reservationInfo.setCustomeName(Common.currentJudet.getName());
                                reservationInfo.setTime(Common.converTimeSlotToString(Common.currentTimeSlot));
                                reservationInfo.setDate(Common.dateDonation.getTime().toString());
                                reservationInfo.setSlot(Long.valueOf(Common.currentTimeSlot));
                                reservationInfo.setCalendar("da");

                                UserInfo userInfo = new UserInfo();
                                userInfo.setCentreId(Common.currentJudet.getJudetId());
                                userInfo.setDate(Common.simpleFormatDate.format(Common.dateDonation.getTime()));
                                userInfo.setJudet(Common.judet);
                                userInfo.setData(Common.dateDonation.getTime());
                                userInfo.setSlot(String.valueOf(Common.currentTimeSlot));
                                userInfo.setTime(Common.converTimeSlotToString(Common.currentTimeSlot));
                                Common.getNotifyAllow = true;

                                DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                        .collection("donationLocation")
                                        .document(Common.judet)
                                        .collection("NewBranch")
                                        .document(Common.currentJudet.getJudetId())
                                        .collection(Common.simpleFormatDate.format(Common.dateDonation.getTime()))
                                        .document(String.valueOf(Common.currentTimeSlot)); //book date

                                bookingDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(getContext(), "A apărut o rezervare în acest interval. Alegeți alt interval. Mulțumim!", Toast.LENGTH_LONG).show();
                                            } else {
                                                bookingDate.set(reservationInfo)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //pentru user
                                                                DocumentReference userBook = FirebaseFirestore.getInstance()
                                                                        .collection("users")
                                                                        .document(mFirebaseAuth.getUid());
                                                                userBook.update("noBookings", ++Common.noBook,
                                                                        "dataDonare", dateLastDonation,
                                                                        "dateBooking", Common.dateDonation.getTime())
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                            }
                                                                        });

                                                                DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                                        .collection("userAccess")
                                                                        .document(mFirebaseAuth.getUid())
                                                                        .collection("Dates")
                                                                        .document(Common.simpleFormatDate.format(Common.dateDonation.getTime()));
                                                                userInfoBook.set(userInfo);

                                                                DocumentReference sexInfo = FirebaseFirestore.getInstance()
                                                                        .collection("donationLocation")
                                                                        .document(Common.judet);
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
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Eroare: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Nu s-a putut face rezervarea. Încercați din nou mai târziu. :)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("NU", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reservationInfo.setTelefon(telefon);
                                reservationInfo.setSex(sex);
                                reservationInfo.setName(name);
                                reservationInfo.setGrupa(grupa);
                                reservationInfo.setEmail(email);
                                reservationInfo.setCentreId(Common.currentJudet.getJudetId());
                                reservationInfo.setCustomeName(Common.currentJudet.getName());
                                reservationInfo.setTime(Common.converTimeSlotToString(Common.currentTimeSlot));
                                reservationInfo.setDate(Common.dateDonation.getTime().toString());
                                reservationInfo.setSlot(Long.valueOf(Common.currentTimeSlot));
                                reservationInfo.setCalendar("nu");

                                UserInfo userInfo = new UserInfo();
                                userInfo.setCentreId(Common.currentJudet.getJudetId());
                                userInfo.setDate(Common.simpleFormatDate.format(Common.dateDonation.getTime()));
                                userInfo.setJudet(Common.judet);
                                userInfo.setData(Common.dateDonation.getTime());
                                userInfo.setSlot(String.valueOf(Common.currentTimeSlot));
                                userInfo.setTime(Common.converTimeSlotToString(Common.currentTimeSlot));
                                Common.getNotifyAllow = true;

                                DocumentReference bookingDate = FirebaseFirestore.getInstance()
                                        .collection("donationLocation")
                                        .document(Common.judet)
                                        .collection("NewBranch")
                                        .document(Common.currentJudet.getJudetId())
                                        .collection(Common.simpleFormatDate.format(Common.dateDonation.getTime()))
                                        .document(String.valueOf(Common.currentTimeSlot)); //book date


                                bookingDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(getContext(), "A apărut o rezervare în acest interval. Alegeți alt interval. Mulțumim!", Toast.LENGTH_LONG).show();
                                            } else {
                                                bookingDate.set(reservationInfo)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getContext(), "Rezervare reușită! Verificați pagina Rezervare actuală.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                //pentru user
                                                DocumentReference userBook = FirebaseFirestore.getInstance()
                                                        .collection("users")
                                                        .document(mFirebaseAuth.getUid());
                                                userBook.update("noBookings", ++Common.noBook,
                                                        "dataDonare", dateLastDonation,
                                                        "dateBooking", Common.dateDonation.getTime())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                            }
                                                        });

                                                DocumentReference userInfoBook = FirebaseFirestore.getInstance()
                                                        .collection("userAccess")
                                                        .document(mFirebaseAuth.getUid())
                                                        .collection("Dates")
                                                        .document(Common.simpleFormatDate.format(Common.dateDonation.getTime()));
                                                userInfoBook.set(userInfo);

                                                DocumentReference sexInfo = FirebaseFirestore.getInstance()
                                                        .collection("donationLocation")
                                                        .document(Common.judet);
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
                                                resetStaticData();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Nu s-a putut face rezervarea. Încercați din nou mai târziu. :)", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                        builder.show();
                    } else if (c2.getTime().before(dateLastDonation)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void resetStaticData() {
        Common.step = 0;
        Common.noBook = 0;
        Common.currentTimeSlot = -1;
        Common.currentJudet = null;
        Common.currentDate.add(Calendar.DATE, 0);
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }

    public void addEventCalendar() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            /*ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);*/
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);
        } else {
            Calendar beginTime;
            beginTime = Common.currentDate;
            Calendar endTime = beginTime;
            //beginTime.add(Calendar.DAY_OF_MONTH, -1);
            int year = beginTime.get(Calendar.YEAR);
            int month = beginTime.get(Calendar.MONTH);
            int day = beginTime.get(Calendar.DAY_OF_MONTH);
            day = day - 1;
            //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
            beginTime.set(year, month, day, 17, 30);
            endTime.set(year, month, day, 17, 30);
            //Toast.makeText(getContext(), beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
            ContentValues l_event = new ContentValues();
            l_event.put("calendar_id", 1);
            l_event.put("title", "ForLife Rezervare Donare de Sânge");
            l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Common.currentTimeSlot));
            l_event.put("eventLocation", Common.currentJudet.getJudetId());
            l_event.put("dtstart", beginTime.getTimeInMillis());
            l_event.put("dtend", endTime.getTimeInMillis());
            l_event.put("hasAlarm", 1);
            l_event.put("allDay", 0);

            l_event.put("eventTimezone", "Romania");
            Uri l_eventUri;
            if (Build.VERSION.SDK_INT >= 8) {
                l_eventUri = Uri.parse("content://com.android.calendar/events");
                Toast.makeText(getContext(), "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
            } else {
                l_eventUri = Uri.parse("content://calendar/events");
                Toast.makeText(getContext(), "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
            }
            Uri l_uri = getContext().getContentResolver()
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
                Uri r_uri = getContext().getContentResolver().insert(reminder_eventUri, reminderValues);
                if (r_uri != null) {
                    long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                }
            }
            Toast.makeText(getContext(), "Rezervare reușită! Verificați pagina Rezervare actuală.", Toast.LENGTH_LONG).show();
            resetStaticData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
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
                //Toast.makeText(getContext(), beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                ContentValues l_event = new ContentValues();
                l_event.put("calendar_id", 1);
                l_event.put("title", "ForLife Rezervare Donare de Sânge");
                l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Common.currentTimeSlot));
                l_event.put("eventLocation", Common.currentJudet.getJudetId());
                l_event.put("dtstart", beginTime.getTimeInMillis());
                l_event.put("dtend", endTime.getTimeInMillis());
                l_event.put("hasAlarm", 1);
                l_event.put("allDay", 0);

                l_event.put("eventTimezone", "Romania");
                Uri l_eventUri;
                if (Build.VERSION.SDK_INT >= 8) {
                    l_eventUri = Uri.parse("content://com.android.calendar/events");
                    Toast.makeText(getContext(), "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
                } else {
                    l_eventUri = Uri.parse("content://calendar/events");
                    Toast.makeText(getContext(), "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                }
                Uri l_uri = getContext().getContentResolver()
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
                    Uri r_uri = getContext().getContentResolver().insert(reminder_eventUri, reminderValues);
                    if (r_uri != null) {
                        long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                    }
                }
                Toast.makeText(getContext(), "Rezervare reușită! Verificați pagina Rezervare actuală.", Toast.LENGTH_LONG).show();
                resetStaticData();
            } else {
                Toast.makeText(getContext(), "Nu s-a permis accesul pentru calendar!", Toast.LENGTH_SHORT).show();
            }
           // return;
        }
        else
        {Toast.makeText(getContext(), "Nu ajunge!", Toast.LENGTH_SHORT).show();}
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void setData() {
        tvTime.setText(Common.converTimeSlotToString(Common.currentTimeSlot));
        //txt_bookTime.setText(simpleDateFormat.format(Common.currentDate.getTime()));
        Common.dateDonation = Common.currentDate;
        txt_bookTime.setText(simpleDateFormat.format(Common.dateDonation.getTime()));
        txt_centreName.setText(Common.currentJudet.getName());
    }

    static Step3Fragment instance;

    public static Step3Fragment getInstance() {
        if (instance == null) {
            instance = new Step3Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //  Toast.makeText(getContext(), mFirebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(mFirebaseUser.getUid());
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, task.getResult().getId());
                                //Toast.makeText(getContext(), task.getResult().getId(), Toast.LENGTH_LONG).show();
                                name = "";
                                sex = "";
                                telefon = "";
                                grupa = "";
                                email = "";
                                greutate = null;
                                varsta = null;
                                dateLastDonation = null;
                                dateDataDonare = null;
                                DocumentSnapshot dc = task.getResult();
                                name = dc.getString("nume");
                                sex = dc.getString("sex");
                                telefon = dc.getString("telefon");
                                grupa = dc.getString("grupaSange");
                                email = mFirebaseAuth.getCurrentUser().getEmail();
                                greutate = Integer.parseInt(dc.get("greutate").toString());
                                varsta = Integer.parseInt(dc.get("varsta").toString());
                                noBookings = Integer.parseInt(dc.get("noBookings").toString());
                                if (noBookings > 0) {
                                    dateLastDonation = dc.getDate("dateBooking");
                                    dateDataDonare = dc.getDate("dataDonare");
                                } else {
                                    dateLastDonation = dc.getDate("dataDonare");
                                }
                                //Toast.makeText(getContext(), dateLastDonation.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        };
        View itemView = inflater.inflate(R.layout.step3_fragment, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }
}
