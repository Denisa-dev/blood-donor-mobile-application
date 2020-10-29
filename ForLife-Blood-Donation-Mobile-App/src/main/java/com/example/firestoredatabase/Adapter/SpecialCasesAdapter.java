package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Model.SpecialCases;
import com.example.firestoredatabase.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SpecialCasesAdapter extends RecyclerView.Adapter<SpecialCasesAdapter.SpecialCasesHolder> {

    private Context mContext;
    private List<SpecialCases> mCases;
    private OnItemClickListener mListener;

    public SpecialCasesAdapter(Context context, List<SpecialCases> cases){
        mContext = context;
        mCases = cases;
    }

    @NonNull
    @Override
    public SpecialCasesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_cases,
                parent, false);
        return new SpecialCasesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialCasesHolder holder, int position) {
        SpecialCases specialCases = mCases.get(position);
        holder.descriere.setText(specialCases.getDescriere());
        holder.noBookings.setText(String.valueOf(specialCases.getNrBookings()));
        holder.tvGrupa.setText(specialCases.getGrupa());
        holder.tvNume.setText(specialCases.getNume());
        holder.tvCentru.setText(specialCases.getCentru());
        Picasso.with(mContext)
                .load(specialCases.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.myImage
                );
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.linearLayout.getVisibility() ==  View.GONE){
                    TransitionManager.beginDelayedTransition(holder.specialCard, new AutoTransition());
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    holder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else
                {
                    TransitionManager.beginDelayedTransition(holder.specialCard, new AutoTransition());
                    holder.linearLayout.setVisibility(View.GONE);
                    holder.arrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black);
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                //sendIntent.putExtra(Intent.EXTRA_SUBJECT, );
                sendIntent.putExtra(Intent.EXTRA_TEXT, "ForLife App te invită să salvezi vieți. Citește descrierea cazului, donează și dă share mai departe.\n\n" + specialCases.getNume()
                        +"\n\n" + specialCases.getGrupa()+  "\n\n" + specialCases.getDescriere() +"\n\n"+ "Sunați la: " + specialCases.getCentru()+ "\n\n" + "Persoana: " + specialCases.getImageUrl());
                mContext.startActivity(Intent.createChooser(sendIntent, "Distribuie cazul prietenilor tăi"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCases.size();
    }

    public class SpecialCasesHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public TextView noBookings, tvGrupa, tvCentru, tvNume, descriere;
        public ImageView myImage;
        Button arrow, share;
        LinearLayout linearLayout;
        CardView specialCard;

        public SpecialCasesHolder(@NonNull View itemView) {
            super(itemView);

            noBookings = itemView.findViewById(R.id.tv_no_booking);
            tvCentru = itemView.findViewById(R.id.tv_centru);
            tvGrupa = itemView.findViewById(R.id.tv_grupa);
            tvNume = itemView.findViewById(R.id.tv_name);
            descriere = itemView.findViewById(R.id.tv_desc);
            myImage = itemView.findViewById(R.id.personImage);
            arrow = itemView.findViewById(R.id.arrow);
            linearLayout = itemView.findViewById(R.id.linearSpecial);
            specialCard = itemView.findViewById(R.id.specialCard);
            share = itemView.findViewById(R.id.share);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.OnItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}
