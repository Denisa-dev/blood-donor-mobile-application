package com.example.firestoredatabase;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.firestoredatabase.dialogs.ConfirmPasswordDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DeleteAccountActivity extends AppCompatActivity implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView email;
    Button delete;
    private static String slot, centru, data, judet, idPerson;
    private static Date lastDate;
    private static Integer no, ident;
    ;
    String id = mAuth.getUid();
    DocumentReference date = FirebaseFirestore.getInstance().collection("users").document(mAuth.getUid());
    public static String password;


    @Override
    public void onConfirmPasswordListener(String pass) {
        password = pass;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), pass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    date.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            no = Integer.parseInt(documentSnapshot.get("noBookings").toString());
                            if (no > 0) {
                                lastDate = documentSnapshot.getDate("dateBooking");
                            } else {
                                lastDate = documentSnapshot.getDate("dataDonare");
                            }
                            if (no > 0 && (lastDate.after(Calendar.getInstance().getTime())
                                    || Common.simpleFormatDate.format(lastDate).equals(Common.simpleFormatDate.format(Calendar.getInstance().getTime())))) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_YYYY");
                                data = sdf.format(lastDate);

                                DocumentReference docAccess = FirebaseFirestore.getInstance().collection("userAccess").document(mAuth.getUid())
                                        .collection("Dates").document(data);
                                docAccess.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        slot = documentSnapshot.getString("slot");
                                        centru = documentSnapshot.getString("centreId");
                                        judet = documentSnapshot.getString("judet");
                                        DocumentReference deleteBook = FirebaseFirestore.getInstance().collection("donationLocation").document(judet)
                                                .collection("NewBranch").document(centru).collection(data).document(slot);

                                        deleteBook.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.getResult().get("identificator") != null) {
                                                    ident = Integer.parseInt(task.getResult().get("identificator").toString());
                                                    idPerson = task.getResult().get("docIDPacient").toString();
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

                                                DocumentReference deleteNoBook = FirebaseFirestore.getInstance()
                                                        .collection("donationLocation")
                                                        .document(judet);
                                                String sex = task.getResult().getString("sex");
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

                                                if (task.getResult().getString("calendar").equals("da")) {
                                                    if (ContextCompat.checkSelfPermission(DeleteAccountActivity.this,
                                                            Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                                        Uri deleteUri = null;
                                                        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(Common.eventID)));
                                                        int rows = getContentResolver().delete(deleteUri, null, null);
                                                    }
                                                }
                                                DocumentReference docAccess2 = FirebaseFirestore.getInstance().collection("userAccess").document(id);
                                                docAccess2.delete();
                                                DocumentReference docUsers = FirebaseFirestore.getInstance().collection("users").document(id);
                                                docUsers.delete();
                                                deleteBook.delete();
                                            }
                                        });
                                    }
                                });
                            } else {
                                DocumentReference docAccess2 = FirebaseFirestore.getInstance().collection("userAccess").document(id);
                                docAccess2.delete();
                                DocumentReference docUsers = FirebaseFirestore.getInstance().collection("users").document(id);
                                docUsers.delete();
                            }
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DeleteAccountActivity.this, "Cont șters permanent!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(DeleteAccountActivity.this, SignInActivity.class));
                                        finish();
                                    }
                                    else
                                        Toast.makeText(DeleteAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(DeleteAccountActivity.this, "Reautentificare nereușită! Nu s-a putut șterge contul.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        delete = findViewById(R.id.delete);
        email = findViewById(R.id.actualEmail);
        email.setText(mAuth.getCurrentUser().getEmail());

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteAccountActivity.this, SettingsActivity.class));
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DeleteAccountActivity.this);
                dialog.setTitle("Sigur vreți să ștergeți contul?");
                dialog.setPositiveButton("Șterge", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
                        dialog.show(getSupportFragmentManager(), "ConfirmPasswordDialog");
                    }
                });
                dialog.setNegativeButton("Renunță", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText || v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        //if (getCurrentFocus() != null) {

        //}
        return super.dispatchTouchEvent(ev);
    }
}
