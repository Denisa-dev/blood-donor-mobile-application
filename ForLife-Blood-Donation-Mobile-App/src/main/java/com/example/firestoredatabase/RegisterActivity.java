package com.example.firestoredatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firestoredatabase.Fragment.FirstFragment;
import com.example.firestoredatabase.Fragment.SecondFragment;
import com.example.firestoredatabase.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;

public class RegisterActivity extends AppCompatActivity {

    String nume, telefon, bloodGroup, email, sex, judet;
    TextInputEditText name, age, phone;
    Integer varsta, greutate;
    String mDate;
    Integer noBookings = 0;
    Button next;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        //email = intent.getStringExtra("email");
        name = findViewById(R.id.full_name_text);

        loadFragment1(new FirstFragment());
    }

    public void loadFragment1(FirstFragment firstFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, firstFragment);
        fragmentTransaction.commit();
    }

    public void loadFragment2(SecondFragment secondFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, secondFragment);
        fragmentTransaction.commit();
    }

    public void ReceiveData(String n, String t, String v, String s) {
        nume = n;
        telefon = t;
        varsta = Integer.parseInt(v);
        sex = s;
    }

    public void ReceiveData2(String j, String b, String d, Integer w) {
        judet = j;
        bloodGroup = b;
        mDate = d;
        greutate = w;
    }

    public void ReceiveData3(String g, String d, String s, String j) {
        mDate = d;
        bloodGroup = g;
        sex = s;
        judet = j;
    }

    public void Save() {
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(mFirebaseAuth.getUid());
        //Toast.makeText(RegisterActivity.this, mFirebaseAuth.getUid(), Toast.LENGTH_LONG).show();
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        email = userF.getEmail();
        Users user = null;
        try {
            user = new Users(nume, varsta, telefon, bloodGroup, email, judet, sex, Common.simpleFormatDate.parse(mDate), noBookings, greutate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setNume(nume);
        user.setVarsta(varsta);
        user.setTelefon(telefon);
        user.setGrupaSange(bloodGroup);
        user.setEmail(email);
        user.setJudet(judet);
        user.setSex(sex);
        user.setGreutate(greutate);
        try {
            user.setDataDonare(Common.simpleFormatDate.parse(mDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        userRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "Salvat cu succes!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "A apărut o eroare. Vă rugăm încercați din nou!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText || v instanceof TextInputEditText)
            {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())){
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

    @Override
    public void onBackPressed() {

        if(nume == null  || telefon == null || bloodGroup == null || judet == null || sex == null)
        {
            Toast.makeText(RegisterActivity.this, "Completați informațiile mai întâi.", Toast.LENGTH_LONG).show();
        }
        else{
            super.onBackPressed();
        }
    }
}
