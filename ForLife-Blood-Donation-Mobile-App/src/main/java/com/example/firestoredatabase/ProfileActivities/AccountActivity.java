package com.example.firestoredatabase.ProfileActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firestoredatabase.HomeActivity;
import com.example.firestoredatabase.Model.Users;
import com.example.firestoredatabase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextJudet;
    private EditText textViewGrupa;
    private EditText telefon, age, email, greutate;
    ImageView edit, kilo, book, ageicon;
    TextView noBook;

    private static Integer position, position2;

    Button update, cancel;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollRef = db.collection("users");
    private DocumentReference userRef = db.document("users/" + mFirebaseAuth.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        editTextName = findViewById(R.id.full_name_text);
        // editTextJudet = findViewById(R.id.judet);
        //textViewGrupa = findViewById(R.id.blood);
        telefon = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email_text);
        noBook = findViewById(R.id.noBooking);
        edit = findViewById(R.id.editImage);
        update = findViewById(R.id.button3);
        cancel = findViewById(R.id.button4);
        greutate = findViewById(R.id.greutate);
        kilo = findViewById(R.id.kilos);
        book = findViewById(R.id.books);
        ageicon = findViewById(R.id.ageicon);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(AccountActivity.this, "Număr rezervări", Toast.LENGTH_SHORT);
                View v = toast.getView();
                //Gets the actual oval background of the Toast then sets the colour filter
                v.getBackground().setColorFilter(getResources().getColor(R.color.lichen3), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                TextView text = v.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                toast.setGravity(Gravity.NO_GRAVITY, 5, 385);
                toast.show();
            }
        });

        kilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(AccountActivity.this, "Greutate", Toast.LENGTH_SHORT);
                View v = toast.getView();
                //Gets the actual oval background of the Toast then sets the colour filter
                v.getBackground().setColorFilter(getResources().getColor(R.color.lichen3), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                TextView text = v.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                toast.setGravity(Gravity.NO_GRAVITY, 0, 260);
                toast.show();
            }
        });

        ageicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(AccountActivity.this, "Vârstă", Toast.LENGTH_SHORT);
                View v = toast.getView();
                //Gets the actual oval background of the Toast then sets the colour filter
                v.getBackground().setColorFilter(getResources().getColor(R.color.lichen3), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                TextView text = v.findViewById(android.R.id.message);
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                toast.setGravity(Gravity.NO_GRAVITY, 5, -100);
                toast.show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                finish();
            }
        });

        Spinner spinner2 = findViewById(R.id.judet);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.judet, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setEnabled(false);
        spinner2.setClickable(false);
        spinner2.setAdapter(adapter2);

        Context context = getApplicationContext();
        String[] judetArray = context.getResources().getStringArray(R.array.judet);

        Spinner spinner = findViewById(R.id.blood);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.blood, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setAdapter(adapter);

        Context context2 = getApplicationContext();
        String[] bloodArray = context2.getResources().getStringArray(R.array.blood);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.exists()) {
                            Users user = queryDocumentSnapshots.toObject(Users.class);
                            for (int i = 0; i < judetArray.length; i++) {
                                if (judetArray[i].equals(user.getJudet()))
                                    position = i;
                            }

                            for (int i = 0; i < bloodArray.length; i++) {
                                if (bloodArray[i].equals(user.getGrupaSange()))
                                    position2 = i;
                            }
                            spinner2.setSelection(position);
                            spinner.setSelection(position2);
                            editTextName.setText(user.getNume());
                            // editTextJudet.setText(user.getJudet());
                            //textViewGrupa.setText(user.getGrupaSange());
                            telefon.setText(user.getTelefon());
                            email.setText(user.getEmail());
                            age.setText(user.getVarsta().toString());
                            noBook.setText(user.getNoBookings().toString());
                            greutate.setText(user.getGreutate().toString());

                        } else {
                            Toast.makeText(AccountActivity.this, "Nu sunteți în baza noastră de date!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountActivity.this, "A apărut o eroare. Încercați mai târziu.", Toast.LENGTH_SHORT).show();
                    }
                });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextName.setEnabled(true);
                telefon.setEnabled(true);
                age.setEnabled(true);
                greutate.setEnabled(true);
                //editTextJudet.setEnabled(true);
                spinner2.setEnabled(true);
                spinner2.setClickable(true);
                spinner.setEnabled(true);
                spinner.setClickable(true);
                // textViewGrupa.setEnabled(true);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String judet = spinner2.getSelectedItem().toString();
                String grupa = spinner.getSelectedItem().toString();
                String tel = telefon.getText().toString();
                String varsta = age.getText().toString();
                String weight = greutate.getText().toString();

                if (!hasValidationErrors(name, tel, grupa, judet, varsta, weight)) {
                    userRef.update(
                            "nume", name,
                            "judet", judet,
                            "telefon", tel,
                            "grupaSange", grupa,
                            "varsta", Integer.parseInt(varsta),
                            "greutate", Integer.parseInt(weight))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AccountActivity.this, "Informații actualizate cu succes!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountActivity.this, "Nu s-a putut face actualizarea. Încercați mai târziu.", Toast.LENGTH_LONG).show();
                        }
                    });
                } //else {
                    //Toast.makeText(AccountActivity.this, "Câmpurile necompletate/completate sunt invalide!", Toast.LENGTH_LONG).show();
                //}
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextName.setEnabled(false);
                telefon.setEnabled(false);
                age.setEnabled(false);
                greutate.setEnabled(false);
                //editTextJudet.setEnabled(false);
                spinner2.setEnabled(false);
                spinner2.setClickable(false);
                spinner.setEnabled(false);
                spinner.setClickable(false);
                //  textViewGrupa.setEnabled(false);

                userRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.exists()) {
                                    Users user = queryDocumentSnapshots.toObject(Users.class);

                                    for (int i = 0; i < judetArray.length; i++) {
                                        if (judetArray[i].equals(user.getJudet()))
                                            position = i;
                                    }

                                    for (int i = 0; i < bloodArray.length; i++) {
                                        if (bloodArray[i].equals(user.getGrupaSange()))
                                            position2 = i;
                                    }

                                    editTextName.setText(user.getNume());
                                    spinner2.setSelection(position);
                                    spinner.setSelection(position2);
                                    // editTextJudet.setText(user.getJudet());
                                    //textViewGrupa.setText(user.getGrupaSange());
                                    telefon.setText(user.getTelefon());
                                    email.setText(user.getEmail());
                                    age.setText(user.getVarsta().toString());
                                    noBook.setText(user.getNoBookings().toString());
                                    greutate.setText(user.getGreutate().toString());

                                } else {
                                    Toast.makeText(AccountActivity.this, "Nu sunteți în baza noastră de date!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AccountActivity.this, "A apărut o eroare. Ne pare rău.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private boolean hasValidationErrors(String name, String tel, String grupa, String judet, String varsta, String weight) {

        if (name.isEmpty()) {
            editTextName.setError("Introduceți un nume");
            editTextName.requestFocus();
            return true;
        }

        if (tel.isEmpty()) {
            telefon.setError("Număr de telefon invalid");
            telefon.requestFocus();
            return true;
        }
        if (tel.length() != 15) {
            telefon.setError("Număr de telefon în formatul inițial!");
            telefon.requestFocus();
            return true;
        }

        if (varsta.isEmpty()) {
            age.setError("Vârstă invalidă!");
            age.requestFocus();
            return true;
        }

        if (varsta.startsWith("0")) {
            age.setError("Vârstă invalidă!");
            age.requestFocus();
            return true;
        }

        if (weight.isEmpty()) {
            greutate.setError("Greutate invalidă!");
            greutate.requestFocus();
            return true;
        }
        if (weight.startsWith("0")) {
            greutate.setError("Greutate invalidă!");
            greutate.requestFocus();
            return true;
        }

        if(tel == null || !tel.matches("^\\+?(40)\\)?[ ]?([1-9]{3})[ ]?([0-9]{3})[ ]?([0-9]{3})$")){
            telefon.setError("Număr de telefon invalid.");
            telefon.requestFocus();
            return true;
        }
        return false;
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AccountActivity.this, HomeActivity.class));
        finish();
    }
}
