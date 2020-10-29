package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.Interfaces.IRecyclerSelectedItemListener;
import com.example.firestoredatabase.Model.TimeSlot;
import com.example.firestoredatabase.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        this.localBroadcastManager =  LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        this.localBroadcastManager =  LocalBroadcastManager.getInstance(context);
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTimeSlot.setText(new StringBuilder(Common.converTimeSlotToString(position)).toString());
        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));


        holder.mTimeSlotDescription.setText("Disponibil");
        holder.mTimeSlotDescription.setTextColor(context.getResources()
                .getColor(android.R.color.white));
        holder.mTimeSlot.setTextColor(context.getResources()
                .getColor(android.R.color.white));
        if(timeSlotList.size() == 0)
        {
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));


            holder.mTimeSlotDescription.setText("Disponibil");
            holder.mTimeSlotDescription.setTextColor(context.getResources()
                    .getColor(android.R.color.white));
            holder.mTimeSlot.setTextColor(context.getResources()
                    .getColor(android.R.color.white));
        }
        else
        {
            for(TimeSlot slotValue:timeSlotList)
            {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == position)
                {
                    holder.card_time_slot.setTag(Common.DISABLE_TAG);
                    holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));

                    holder.mTimeSlotDescription.setText("Ocupat");
                    holder.mTimeSlotDescription.setTextColor(context.getResources()
                            .getColor(android.R.color.white));

                    holder.mTimeSlot.setTextColor(context.getResources()
                            .getColor(android.R.color.white));
                }
            }
        }
        if(!cardViewList.contains(holder.card_time_slot))
        {
            cardViewList.add(holder.card_time_slot);
        }

        holder.setiRecyclerSelectedItemListener(new IRecyclerSelectedItemListener() {
            @Override
            public void onItemSelectedListener(View view, int i) {
                for(CardView cardView:cardViewList)
                {
                    if(cardView.getCardBackgroundColor().getDefaultColor() == context.getResources()
                            .getColor(android.R.color.holo_red_light) && holder.card_time_slot.getTag() == Common.DISABLE_TAG)
                        //cardView.setBackgroundColor(context.getResources()
                                //.getColor(android.R.color.holo_green_light));
                         Toast.makeText(context, "Acest interval este deja ocupat!", Toast.LENGTH_LONG).show();
                    else if (holder.card_time_slot.getTag() != Common.DISABLE_TAG && cardView.getTag() == null ) {
                            cardView.setBackgroundColor(context.getResources()
                                    .getColor(android.R.color.holo_green_light));

                    }
                    /*holder.mTimeSlotDescription.setTextColor(context.getResources()
                            .getColor(android.R.color.white));

                    holder.mTimeSlot.setTextColor(context.getResources()
                            .getColor(android.R.color.white));*/
                }

                if(holder.card_time_slot.getTag() == Common.DISABLE_TAG)
                {
                    Toast.makeText(context, "Acest interval este deja ocupat!", Toast.LENGTH_LONG).show();

                }
                //holder.card_time_slot.getTag() != Common.DISABLE_TAG
                else if(holder.card_time_slot.getCardBackgroundColor().getDefaultColor() == context.getResources()
                    .getColor(android.R.color.holo_green_light))
                {
                    holder.card_time_slot.setBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_red_light));

                        Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                        intent.putExtra(Common.KEY_TIME_SLOT, position);
                        intent.putExtra(Common.KEY_STEP, 2);
                        localBroadcastManager.sendBroadcast(intent);

                }
                /*if(holder.card_time_slot.getCardBackgroundColor().getDefaultColor() == context.getResources()
                        .getColor(android.R.color.holo_red_light) && holder.card_time_slot.getTag() != Common.DISABLE_TAG)
                {
                    holder.card_time_slot.setBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_green_light));
                }*/
               /* else {
                    holder.card_time_slot.setBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_red_light));

                    Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_TIME_SLOT, position);
                    intent.putExtra(Common.KEY_STEP, 2);
                    localBroadcastManager.sendBroadcast(intent);}*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTimeSlot, mTimeSlotDescription;
        CardView card_time_slot;

        IRecyclerSelectedItemListener iRecyclerSelectedItemListener;

        public void setiRecyclerSelectedItemListener(IRecyclerSelectedItemListener iRecyclerSelectedItemListener) {
            this.iRecyclerSelectedItemListener = iRecyclerSelectedItemListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = itemView.findViewById(R.id.card_slot);
            mTimeSlot = itemView.findViewById(R.id.txt_time_slot);
            mTimeSlotDescription = itemView.findViewById(R.id.txt_slot_description);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerSelectedItemListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
