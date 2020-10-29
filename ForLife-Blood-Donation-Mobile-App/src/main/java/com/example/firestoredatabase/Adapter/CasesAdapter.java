package com.example.firestoredatabase.Adapter;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Model.Cases;
import com.example.firestoredatabase.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CasesAdapter extends FirestoreRecyclerAdapter<Cases, CasesAdapter.CasesHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CasesAdapter(@NonNull FirestoreRecyclerOptions<Cases> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CasesHolder casesHolder, int i, @NonNull Cases cases) {
        casesHolder.pacient.setText(cases.getPacient());
        casesHolder.centru.setText(cases.getCentru());
        casesHolder.caz.setText(cases.getCaz());
        casesHolder.parere.setText(cases.getOpinie());
        casesHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(casesHolder.linearLayout.getVisibility() ==  View.GONE){
                    TransitionManager.beginDelayedTransition(casesHolder.happyCard, new AutoTransition());
                    casesHolder.linearLayout.setVisibility(View.VISIBLE);
                    casesHolder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
                else
                {
                    TransitionManager.beginDelayedTransition(casesHolder.happyCard, new AutoTransition());
                    casesHolder.linearLayout.setVisibility(View.GONE);
                    casesHolder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });
    }

    @NonNull
    @Override
    public CasesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.happy_item,
                parent, false);
        return new CasesHolder(view);
    }

    public class CasesHolder  extends RecyclerView.ViewHolder {
        TextView pacient, caz, parere, centru;
        Button arrow;
        LinearLayout linearLayout;
        CardView happyCard;

        public CasesHolder(@NonNull View itemView) {
            super(itemView);

            pacient = itemView.findViewById(R.id.numePacient);
            caz = itemView.findViewById(R.id.caz);
            parere = itemView.findViewById(R.id.parere);
            centru = itemView.findViewById(R.id.centru);
            arrow = itemView.findViewById(R.id.arrow);
            linearLayout = itemView.findViewById(R.id.happyExpandable);
            happyCard = itemView.findViewById(R.id.happyEvent);
        }
    }
}
