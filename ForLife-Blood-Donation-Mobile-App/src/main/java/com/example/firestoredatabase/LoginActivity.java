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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firestoredatabase.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {

    EditText emailId, password, pass2;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        pass2 = findViewById(R.id.editText3);
        btnSignUp = findViewById(R.id.button2);
        tvSignIn = findViewById(R.id.txtBack2);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                Users user = new Users();
                user.setEmail(email);
                String pwd = password.getText().toString();
                String pwd2 = pass2.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Introduceți un email.");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Introduceți parola");
                    password.requestFocus();
                } else if(pwd2.isEmpty()){
                    password.setError("Introduceți parola");
                    password.requestFocus();
                }
                else if (email.isEmpty() && pwd.isEmpty() && pwd2.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Câmpurile nu sunt completate!", Toast.LENGTH_SHORT).show();
                }
                else if (!pwd.equals(pwd2)) {
                    Toast.makeText(LoginActivity.this, "Parola nu se potrivește cu cea de confirmare!", Toast.LENGTH_LONG).show();
                }
                else if (pwd.length() < 6 || pwd2.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Parola trebuie să conțină minim 6 caractere!", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty() && pwd2.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    if(task.getResult().getSignInMethods().size() == 1)
                                    {
                                        Toast.makeText(LoginActivity.this, "Acest email este deja folosit!", Toast.LENGTH_LONG).show();
                                    } else
                                    {
                                        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(LoginActivity.this, "Eroare: " + error, Toast.LENGTH_LONG).show();
                                                } else {
                                                    sendEmailVerification();
                                                }
                                            }
                                        });
                                    }
                                }catch (NullPointerException e){
                                    Toast.makeText(LoginActivity.this, "Eroare: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Eroare de server sau email gresit!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });

    }

    private void sendEmailVerification(){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Înregistrare reușită."+"\n"+"Verificați email-ul.", Toast.LENGTH_LONG).show();
                        sendUserToSignInActivity();
                        mFirebaseAuth.signOut();
                    }
                    else
                    {
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Eroare: " + error, Toast.LENGTH_LONG).show();
                        mFirebaseAuth.signOut();
                    }
                }
            });
        }
    }

    private void sendUserToSignInActivity() {
        Intent intToHome = new Intent(LoginActivity.this, SignInActivity.class);
        intToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intToHome);
        finish();
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
}
