package com.example.firestoredatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    EditText emailId;
    TextInputEditText password;
    Button btnSignIn;
    TextView tvSignUp, tvPass;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthListener;
    private  static Boolean emailCheck;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnSignIn = findViewById(R.id.button2);
        tvSignUp = findViewById(R.id.textView);
        tvPass = findViewById(R.id.passForgot);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    Log.d(TAG, "User logat");
                }
                else{
                    Log.d(TAG, "User loged out");
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Nu ați introdus email-ul.");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Nu ați introdus parola.");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(SignInActivity.this,"Câmpurile de completat sunt goale.",Toast.LENGTH_LONG).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){

                    mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    if (task.getResult().getSignInMethods().size() == 0) {
                                        Toast.makeText(SignInActivity.this, "Nu aveți creat un cont. Creează cont mai jos. :)", Toast.LENGTH_LONG).show();
                                    } else {
                                        mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(!task.isSuccessful()){
                                                    Toast.makeText(SignInActivity.this,"Parola și/sau email-ul sunt greșite!",Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    verifyEmail();
                                                }
                                            }
                                        });
                                    }
                                } catch (NullPointerException e) {
                                    Toast.makeText(SignInActivity.this, "Eroare: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignInActivity.this,"A apărut o eroare. Încercați mai târziu.",Toast.LENGTH_LONG).show();
                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intSignUp);
                emailId.setText("");
                password.setText("");
            }
        });

        tvPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
                emailId.setText("");
                password.setText("");
            }
        });
    }

    private  void verifyEmail(){
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        emailCheck = firebaseUser.isEmailVerified();
        if(emailCheck)
        {
            sendUserToHomeActivity();
        }
        else
        {
            Toast.makeText(SignInActivity.this, "Verificați contul de email mai întâi.", Toast.LENGTH_LONG).show();
            mFirebaseAuth.signOut();
        }
    }

    private void sendUserToHomeActivity() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(mFirebaseAuth.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        loginActivity(true);
                    } else {
                        loginActivity(false);
                    }
                } else {
                    Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginActivity(Boolean flag){
        if(flag == true){
            Toast.makeText(SignInActivity.this,"Autentificare cu succes!",Toast.LENGTH_LONG).show();
            Intent intToHome = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intToHome);
            emailId.setText("");
            password.setText("");
            finish();
        }
        else
        {
            Intent i = new Intent(SignInActivity.this, RegisterActivity.class);
            startActivity(i);
            emailId.setText("");
            password.setText("");
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText)
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
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
}
