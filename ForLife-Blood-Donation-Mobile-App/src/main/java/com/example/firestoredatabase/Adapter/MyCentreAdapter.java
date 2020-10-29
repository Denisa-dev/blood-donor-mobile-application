package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.Interfaces.IRecyclerSelectedItemListener;
import com.example.firestoredatabase.Model.Judet;
import com.example.firestoredatabase.R;

import java.util.ArrayList;
import java.util.List;

public class MyCentreAdapter extends RecyclerView.Adapter<MyCentreAdapter.MyViewHolder> {

    Context context;
    List<Judet> judetList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyCentreAdapter(Context context, List<Judet> judetList) {
        this.context = context;
        this.judetList = judetList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtCenter;
        CardView card_judet;

        IRecyclerSelectedItemListener iRecyclerSelectedItemListener;

        public void setiRecyclerSelectedItemListener(IRecyclerSelectedItemListener iRecyclerSelectedItemListener) {
            this.iRecyclerSelectedItemListener = iRecyclerSelectedItemListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_judet = itemView.findViewById(R.id.card_judet);
            txtCenter = itemView.findViewById(R.id.txt_centru);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerSelectedItemListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_judet, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtCenter.setText(judetList.get(position).getName());
        if(!cardViewList.contains(holder.card_judet)){
            cardViewList.add(holder.card_judet);
        }
        holder.setiRecyclerSelectedItemListener(new IRecyclerSelectedItemListener() {
            @Override
            public void onItemSelectedListener(View view, int i) {
                //set white background for all card not be selected
                for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                //set selected bg for only selected item

                holder.card_judet.setCardBackgroundColor(context.getResources()
                        .getColor(R.color.blush2));

                    //send broadcast to tell resAct to enable next button
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_CENTRE_SELECTED, judetList.get(i));
                intent.putExtra(Common.KEY_STEP, 1);
                localBroadcastManager.sendBroadcast(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return judetList.size();
    }


}
