package com.example.firestoredatabase.Fragment;


import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.NotificationAdapter;
import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.HomeActivity;
import com.example.firestoredatabase.Model.Notification;
import com.example.firestoredatabase.PopupNotificationActivity;
import com.example.firestoredatabase.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import javax.annotation.Nullable;

import static com.example.firestoredatabase.Model.NotificationChannel.CHANNEL_1_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.OnItemClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static Integer count;
    View view;
    Button all, one;
    @ServerTimestamp
    FieldValue date = FieldValue.serverTimestamp();
    NotificationAdapter adapter;
    private FirestoreRecyclerOptions<Notification> options;
    private static final String TAG = "NotificationFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("Notifications");
    private DocumentReference userRef = db.collection("users").document(mAuth.getUid());
    private RecyclerView recyclerView;
    private NotificationManagerCompat notificationManagerCompat;
    private static String judet, grupa;
    public static final int LOAD_FRAGMENT = 1;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public NotificationFragment(String judet, String grupa) {
        this.judet = judet;
        this.grupa = grupa;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.recycler_notification);
        one = view.findViewById(R.id.my_judet);
        all = view.findViewById(R.id.all_judet);

        //Toast.makeText(getContext(), this.judet + this.grupa, Toast.LENGTH_LONG).show();

        if (Common.notifyFlag == false) {
            all.setTextColor(getResources().getColor(R.color.white));
            all.setBackground(getResources().getDrawable(R.drawable.border_button_on));
            one.setTextColor(getResources().getColor(R.color.dark));
            one.setBackground(getResources().getDrawable(R.drawable.border_button_off));
        } else {
            one.setTextColor(getResources().getColor(R.color.white));
            one.setBackground(getResources().getDrawable(R.drawable.border_button_on));
            all.setTextColor(getResources().getColor(R.color.dark));
            all.setBackground(getResources().getDrawable(R.drawable.border_button_off));
        }

        notificationManagerCompat = NotificationManagerCompat.from(getContext());

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.notifyFlag = true;
                onStart();
                one.setTextColor(getResources().getColor(R.color.white));
                one.setBackground(getResources().getDrawable(R.drawable.border_button_on));
                all.setTextColor(getResources().getColor(R.color.dark));
                all.setBackground(getResources().getDrawable(R.drawable.border_button_off));
                adapter = new NotificationAdapter(options, Common.notifyFlag, judet, grupa);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setUserVisibility(true);
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.notifyFlag = false;
                onStart();
                all.setTextColor(getResources().getColor(R.color.white));
                all.setBackground(getResources().getDrawable(R.drawable.border_button_on));
                one.setTextColor(getResources().getColor(R.color.dark));
                one.setBackground(getResources().getDrawable(R.drawable.border_button_off));
                adapter = new NotificationAdapter(options, Common.notifyFlag, judet, grupa);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setUserVisibility(true);
            }
        });
        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        Query query = noteRef
                .orderBy("data", Query.Direction.DESCENDING);

        options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();
        adapter = new NotificationAdapter(options, Common.notifyFlag, judet, grupa);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Notification notes = documentSnapshot.toObject(Notification.class);

               /* PopupNotificationDialog popupNotificationDialog = new PopupNotificationDialog();

                Bundle bundle = new Bundle();
                bundle.putString("centru", notes.getCentru());
                bundle.putString("titlu", notes.getTitlu());
                bundle.putString("desc", notes.getDescriere());
                bundle.putString("grupa", notes.getGrupa());
                bundle.putString("judet", notes.getJudet());
                popupNotificationDialog.setArguments(bundle);
                popupNotificationDialog.setTargetFragment(NotificationFragment.this, LOAD_FRAGMENT);
                popupNotificationDialog.show(getFragmentManager(), "ConfirmLoadDialog");*/

                Intent i = new Intent(getContext(), PopupNotificationActivity.class);
                i.putExtra("centru", notes.getCentru());
                i.putExtra("titlu", notes.getTitlu());
                i.putExtra("desc", notes.getDescriere());
                i.putExtra("grupa", notes.getGrupa());
                i.putExtra("judet", notes.getJudet());
                startActivity(i);
                //getActivity().finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Date date = new Date();

        if (Common.notifyFlag == false) {
            noteRef.whereEqualTo("grupa", grupa)
                    .orderBy("data").startAt(date).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null)
                        return;
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            sendOnChannel1Notification();
                            adapter = new NotificationAdapter(options, Common.notifyFlag, judet, grupa);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            setUserVisibility(true);
                        }
                    }
                }
            });
        } else if (Common.notifyFlag == true) {
            noteRef.whereEqualTo("grupa", grupa)
                    .whereEqualTo("judet", judet)
                    .orderBy("data").startAt(date).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null)
                        return;
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            sendOnChannel1Notification();
                            adapter = new NotificationAdapter(options, Common.notifyFlag, judet, grupa);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            setUserVisibility(true);
                        }
                    }
                }
            });
        }
        adapter.startListening();
    }

    public void sendOnChannel1Notification() {

        Intent openActivity = new Intent(getContext(), HomeActivity.class);
        openActivity.putExtra("OpenTab", 1);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
                openActivity, 0);

        android.app.Notification notification = new NotificationCompat.Builder(getContext(),
                CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("ForLife Urgență Sânge")
                .setContentText("Atingeți pentru mai multe detalii.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

    //nu vreau sa il opresc din a incarca informatii cand nu e in activitate
    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }

    public void setUserVisibility(boolean myFlag) {
        if (myFlag) {
            if (getFragmentManager() != null) {

                getFragmentManager()
                        .beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
            }
        }
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }
}