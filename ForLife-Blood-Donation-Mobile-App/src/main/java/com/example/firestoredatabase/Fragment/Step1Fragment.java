package com.example.firestoredatabase.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Adapter.MyCentreAdapter;
import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.Interfaces.IAllCenterLoadListenter;
import com.example.firestoredatabase.Interfaces.IJudetListener;
import com.example.firestoredatabase.Model.Judet;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.SpacesItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class Step1Fragment extends Fragment implements IJudetListener, IAllCenterLoadListenter {

    CollectionReference judetRef;
    CollectionReference branchRef;

    IJudetListener iJudetListener;
    IAllCenterLoadListenter iAllCenterLoadListenter;

    @BindView(R.id.mSpinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_judet)
    RecyclerView recycler_judet;

    Unbinder unbinder;

    AlertDialog dialog;

    static Step1Fragment instance;

    public static Step1Fragment getInstance() {
        if(instance == null)
        {instance = new Step1Fragment();}
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        judetRef = FirebaseFirestore.getInstance().collection("donationLocation");
        iJudetListener = this;
        iAllCenterLoadListenter = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.step1_fragment, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        recycler_judet.addItemDecoration(new SpacesItemDecoration(4));
        recycler_judet.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        initView();
        loadJudet();
        return itemView;
    }

    private void initView() {
        recycler_judet.setHasFixedSize(true);
    }

    private void loadJudet() {
        judetRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<String> list = new ArrayList<>();
                    list.add("Alegeți județul în care vreți să donați");
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                        list.add(documentSnapshot.getId());
                    iJudetListener.onAllJudetLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iJudetListener.onAllJudetLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllJudetLoadSuccess(List<String> judetNameList) {
        spinner.setItems(judetNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position > 0){
                    loadBranchCenter(item.toString());
                }
                else
                {
                    recycler_judet.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadBranchCenter(String judetName) {
        dialog.show();

        Common.judet = judetName;

        branchRef = FirebaseFirestore.getInstance()
                .collection("donationLocation")
                .document(judetName)
                .collection("NewBranch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Judet> list = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult())
                    {
                        Judet judet = documentSnapshot.toObject(Judet.class);
                        judet.setJudetId(documentSnapshot.getId());
                        Common.currentCentre = judet.getJudetId();
                        //Toast.makeText(getContext(), Common.currentCentre, Toast.LENGTH_LONG).show();
                        list.add(judet);
                    }
                    iAllCenterLoadListenter.onAllCenterLoadSuccess(list);
                    //Intent intent = new Intent(Common.KEY_CENTRE_LOAD_DONE);
                    //intent.putParcelableArrayListExtra(Common.KEY_CENTRE_LOAD_DONE, list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllCenterLoadListenter.onAllCenterLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllJudetLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAllCenterLoadSuccess(List<Judet> judetNameList) {
        MyCentreAdapter adapter = new MyCentreAdapter(getActivity(), judetNameList);
        recycler_judet.setAdapter(adapter);
        recycler_judet.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onAllCenterLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        dialog.dismiss();
    }
}
