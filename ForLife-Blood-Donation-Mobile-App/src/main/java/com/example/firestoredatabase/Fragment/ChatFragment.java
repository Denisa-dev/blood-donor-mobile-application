package com.example.firestoredatabase.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.ChatAdapter;
import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.Model.EmailContactList;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.SendEmailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private List<EmailContactList> cards;
    private RecyclerView.LayoutManager layoutManager;
    ChatAdapter adapter;
    public static List<String> list;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_chat);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        cards = new ArrayList<>();
        loadCards();
        return view;
    }

    private void loadCards() {
        Context context = getContext();
        String[] centreArray = context.getResources().getStringArray(R.array.centres);
        String[] judetArray = context.getResources().getStringArray(R.array.judet);
        for (int i = 0; i < centreArray.length; i++)
        {
            cards.add(new EmailContactList(centreArray[i],
                    Common.matchJudet(centreArray[i])));
            adapter = new ChatAdapter(cards, getContext());
            recyclerView.setAdapter(adapter);
        }

        adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DocumentReference getInfoPhone = FirebaseFirestore.getInstance()
                        .collection("donationLocation")
                        .document(Common.matchJudet(centreArray[position]))
                        .collection("NewBranch")
                        .document(centreArray[position]);
                getInfoPhone.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Context context = getContext();
                        String[] emails = context.getResources().getStringArray(R.array.emails);
                        Intent intent = new Intent(getContext(), SendEmailActivity.class);
                        if(documentSnapshot.get("telefon") == null){
                            DocumentReference getInfoPhone2 = FirebaseFirestore.getInstance()
                                    .collection("donationLocation")
                                    .document(Common.matchJudet(centreArray[position]))
                                    .collection("NewBranch")
                                    .document(Common.matchCentru(Common.matchJudet(centreArray[position])));
                            getInfoPhone2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    list = (List<String>) documentSnapshot2.get("telefon");
                                    intent.putExtra("email", "enisierosu@gmail.com");
                                    intent.putExtra("centru", emails[position]);
                                    intent.putExtra("telefon", list.get(0));
                                    startActivity(intent);
                                }
                            });

                        }
                        else
                        {
                            list = (List<String>) documentSnapshot.get("telefon");
                            intent.putExtra("email", "enisierosu@gmail.com");
                            intent.putExtra("centru", emails[position]);
                            intent.putExtra("telefon", list.get(0));
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
