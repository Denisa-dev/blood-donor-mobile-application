package com.example.firestoredatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.ListImagesAdapter;
import com.example.firestoredatabase.Model.ImagesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class IstoricActivity extends AppCompatActivity implements ListImagesAdapter.OnItemClickListener {

    private RecyclerView mRecycleview;
    private ListImagesAdapter mAdapter;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference userRef = FirebaseFirestore.getInstance().collection("userAccess")
            .document(mAuth.getUid()).collection("Dates");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istoric);
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = (TextView)toolbar.findViewById(R.id.tvsetBar);
        textView.setText("Istoric");

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        init();
        addList();

        mAdapter.setOnItemClickListener(new ListImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ImagesAdapter dates = documentSnapshot.toObject(ImagesAdapter.class);
                String id = documentSnapshot.getId();
                Intent i = new Intent(IstoricActivity.this, PopupIstoricActivity.class);
                i.putExtra("date", dates.getDate());
                i.putExtra("time", dates.getTime());
                i.putExtra("centreId", dates.getCentreId());
                startActivity(i);
            }
        });
    }

    private void init(){
        mRecycleview = findViewById(R.id.history_recycler);
    }
    private void addList(){

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(IstoricActivity.this, "Eroare: " + e.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (queryDocumentSnapshots.size() <= 0) {
                    Toast.makeText(IstoricActivity.this, "Nu aveți un istoric al rezervărilor momentan.", Toast.LENGTH_LONG).show();
                }
            }
        });
        Query query = userRef.orderBy("data", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ImagesAdapter> options = new FirestoreRecyclerOptions.Builder<ImagesAdapter>()
                .setQuery(query, ImagesAdapter.class)
                .build();


        mAdapter = new ListImagesAdapter(options);

        mRecycleview.setHasFixedSize(true);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mRecycleview.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }
}
