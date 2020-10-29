package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firestoredatabase.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextCity;
    private TextView textViewData;
    Button logoutBtn;
    Button mapBtn;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollRef = db.collection("users");
    private DocumentReference userRef = db.document("users/" + mFirebaseAuth.getUid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextName = findViewById(R.id.edit_text_name);
        editTextAge = findViewById(R.id.edit_text_age);
        editTextCity = findViewById(R.id.edit_text_city);
        textViewData = findViewById(R.id.text_view_data);
        logoutBtn = findViewById(R.id.logoutBtn);
        mapBtn = findViewById(R.id.btnMap);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intToMain);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(MainActivity.this, FinalMapActivity.class);
                startActivity(intToMain);
            }
        });
    }

    public void addUsers(View v) {
        String name = editTextName.getText().toString();
        Integer age = Integer.valueOf(editTextAge.getText().toString());
        String city = editTextCity.getText().toString();

        /*TestUser user = new TestUser(city, name, age);

        userRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Info user saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener(){
                    public void onFailure(@NonNull Exception e){
                        Toast.makeText(MainActivity.this, "Error saving info!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });*/
    }

    public void loadUsers(View v) {
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.exists()) {
                            Users user = queryDocumentSnapshots.toObject(Users.class);
                            String name = user.getNume();
                            Integer age = user.getVarsta();
                            String data = "";
                            data += "Name: " + name +  "\nAge: " + age + "\n\n";
                            textViewData.setText(data);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Documentul nu exista!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Eroare!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
         }

}
