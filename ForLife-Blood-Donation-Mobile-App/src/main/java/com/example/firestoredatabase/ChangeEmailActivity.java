package com.example.firestoredatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firestoredatabase.dialogs.ConfirmPasswordDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ChangeEmailActivity extends AppCompatActivity implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    EditText email;
    TextView confirm;
    public static String password;

    @Override
    public void onConfirmPasswordListener(String pass) {
        password = pass;
        // Toast.makeText(ChangeEmailActivity.this, pass, Toast.LENGTH_SHORT).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(mFirebaseAuth.getCurrentUser().getEmail(), pass);

        // Prompt the user to re-provide their sign-in credentials

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getSignInMethods().size() == 1) {
                                                Toast.makeText(ChangeEmailActivity.this, "Acest email este deja folosit!", Toast.LENGTH_LONG).show();
                                            } else {
                                                mFirebaseAuth.getCurrentUser().updateEmail(email.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    sendEmailVerification();
                                                                    //Toast.makeText(ChangeEmailActivity.this, "Email actualizat cu succes!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } catch (NullPointerException e) {
                                            Toast.makeText(ChangeEmailActivity.this, "Eroare: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        } else
                            Toast.makeText(ChangeEmailActivity.this, "Reautentificare nereușită!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Email actualizat. " + "\n" + "Verificați noul email.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChangeEmailActivity.this, SignInActivity.class));
                    mFirebaseAuth.signOut();
                } else {
                    Toast.makeText(ChangeEmailActivity.this, "Nu s-a reușit actualizarea.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        email = findViewById(R.id.email);
        confirm = findViewById(R.id.save);
        TextView actual = findViewById(R.id.actualEmail);
        actual.setText(mFirebaseAuth.getCurrentUser().getEmail());

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(ChangeEmailActivity.this, "Introduceți un email!", Toast.LENGTH_LONG).show();
                } else {
                    if (!mFirebaseAuth.getCurrentUser().getEmail().equals(email.getText().toString())) {
                        ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
                        dialog.show(getSupportFragmentManager(), "ConfirmPasswordDialog");
                    } else {
                        Toast.makeText(ChangeEmailActivity.this, "Nu puteți introduce același email!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
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
