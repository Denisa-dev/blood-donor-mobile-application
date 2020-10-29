package com.example.firestoredatabase.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.SpecialCasesAdapter;
import com.example.firestoredatabase.Model.SpecialCases;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.SpecialCasesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CasesFragment extends Fragment implements SpecialCasesAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    SpecialCasesAdapter specialCasesAdapter;
    private CollectionReference caseRef = FirebaseFirestore.getInstance().collection("specialCases");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("cases");
    private List<SpecialCases> mCases;
    private static String grupa;
    private int position;
    private static String docId[];

    public CasesFragment() {
        // Required empty public constructor
    }

    public CasesFragment(String grupa){
        this.grupa = grupa;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cases, container, false);
        //StorageReference imagesRef = storageRef.child("cases/girl.jpg");

        mRecyclerView = v.findViewById(R.id.recycler_special);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCases = new ArrayList<>();
        caseRef.orderBy("data", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Eroare: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                docId = new String[queryDocumentSnapshots.size()];
                mCases.clear();
                int i = 0;
                for(DocumentSnapshot dc: queryDocumentSnapshots){
                    docId[i] = dc.getId();
                    SpecialCases specialCases = dc.toObject(SpecialCases.class);
                    mCases.add(specialCases);
                    i++;
                }
                specialCasesAdapter = new SpecialCasesAdapter(getContext(), mCases);
                mRecyclerView.setAdapter(specialCasesAdapter);

                specialCasesAdapter.setOnItemClickListener(CasesFragment.this);
                //specialCasesAdapter.notifyDataSetChanged();
                //mCases.clear();
            }
        });
        return v;
    }

    @Override
    public void OnItemClick(int position) {
        DocumentReference centreRef = FirebaseFirestore.getInstance()
                .collection("donationLocation")
                .document(mCases.get(position).getJudet())
                .collection("NewBranch")
                .document(mCases.get(position).getCentru());

        centreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Intent intent = new Intent(getContext(), SpecialCasesActivity.class);
                        intent.putExtra("centru", mCases.get(position).getCentru());
                        intent.putExtra("judet", mCases.get(position).getJudet());
                        intent.putExtra("docID", docId[position]);
                        intent.putExtra("numePacient", mCases.get(position).getNume());
                        startActivity(intent);
                    } else {
                        CollectionReference col = FirebaseFirestore.getInstance().collection("donationLocation");
                        col.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot dc : task.getResult()) {
                                        if (dc.getId().equals(mCases.get(position).getJudet())) {
                                            //Toast.makeText(PopupNotificationActivity.this, judet, Toast.LENGTH_LONG).show();
                                            CollectionReference doc = FirebaseFirestore.getInstance().collection("donationLocation")
                                                    .document(dc.getId()).collection("NewBranch");
                                            doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (DocumentSnapshot myDoc : task.getResult()) {
                                                        if (myDoc.getId().compareTo(mCases.get(position).getCentru()) != 0) {
                                                            //Toast.makeText(PopupNotificationActivity.this, myDoc.getId(), Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(getContext(), SpecialCasesActivity.class);
                                                            intent.putExtra("centru", mCases.get(position).getCentru());
                                                            intent.putExtra("judet", mCases.get(position).getJudet());
                                                            intent.putExtra("docID", docId[position]);
                                                            intent.putExtra("numePacient", mCases.get(position).getNume());
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            //Intent intent = new Intent(getContext(), ReservationActivity.class);
                                           // startActivity(intent);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
