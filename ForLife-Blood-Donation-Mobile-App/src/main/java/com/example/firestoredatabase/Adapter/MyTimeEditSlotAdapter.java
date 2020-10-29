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
import com.example.firestoredatabase.Interfaces.IRecyclerSelectedItemEditListener;
import com.example.firestoredatabase.Model.TimesEditSlot;
import com.example.firestoredatabase.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimeEditSlotAdapter extends RecyclerView.Adapter<MyTimeEditSlotAdapter.MyViewHolder> {

    Context context;
    List<TimesEditSlot> timeSlotList;
    List<CardView> cardViewList;
    Long slot;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeEditSlotAdapter(Context context, List<TimesEditSlot> timeSlotList, Long slot) {
        this.context = context;
        this.slot = slot;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
        Common.mySlot = this.slot;
    }

    public MyTimeEditSlotAdapter(Context context) {
        this.context = context;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.timeSlotList = new ArrayList<>();
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
        if (timeSlotList.size() == 0) {
            holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));


            holder.mTimeSlotDescription.setText("Disponibil");
            holder.mTimeSlotDescription.setTextColor(context.getResources()
                    .getColor(android.R.color.white));
            holder.mTimeSlot.setTextColor(context.getResources()
                    .getColor(android.R.color.white));
        } else {
            for (TimesEditSlot slotValue : timeSlotList) {
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if (slot == position) {
                    if (position == this.slot) {
                        holder.card_time_slot.setTag(Common.DISABLE_TAG);
                        holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                        holder.mTimeSlotDescription.setText("Rezervarea mea");
                        holder.mTimeSlotDescription.setTextColor(context.getResources()
                                .getColor(android.R.color.white));

                        holder.mTimeSlot.setTextColor(context.getResources()
                                .getColor(android.R.color.white));
                    } else {
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
        }
        if (!cardViewList.contains(holder.card_time_slot)) {
            cardViewList.add(holder.card_time_slot);
        }

        holder.setiRecyclerSelectedItemEditListener(new IRecyclerSelectedItemEditListener() {
            @Override
            public void onItemSelectedListener(View view, int i) {
                for (CardView cardView : cardViewList) {
                     if (holder.card_time_slot.getTag() != Common.DISABLE_TAG && cardView.getTag() == null) {
                        cardView.setBackgroundColor(context.getResources()
                                .getColor(android.R.color.holo_green_light));
                    } else if (cardView.getTag() == null && holder.card_time_slot.getCardBackgroundColor().getDefaultColor() == context.getResources()
                            .getColor(android.R.color.holo_red_light)) {
                        cardView.setBackgroundColor(context.getResources()
                                .getColor(android.R.color.holo_green_light));

                         Intent intent2 = new Intent("sendPosition");
                         intent2.putExtra("position", 98);
                         localBroadcastManager.sendBroadcast(intent2);

                         Intent intent = new Intent("sendFlag");
                         intent.putExtra("flag", 97);
                         localBroadcastManager.sendBroadcast(intent);
                    }
                }

                if (holder.card_time_slot.getTag() == Common.DISABLE_TAG && holder.mTimeSlotDescription.getText() == "Ocupat") {
                    Toast.makeText(context, "Acest interval este deja ocupat!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("sendFlag");
                    intent.putExtra("flag", 97);
                    localBroadcastManager.sendBroadcast(intent);

                    Intent intent2 = new Intent("sendPosition");
                    intent2.putExtra("position", 98);
                    localBroadcastManager.sendBroadcast(intent2);
                } else if (holder.card_time_slot.getTag() == Common.DISABLE_TAG
                        && holder.mTimeSlotDescription.getText() == "Rezervarea mea") {
                    //holder.card_time_slot.setBackgroundColor(context.getResources()
                    //     .getColor(android.R.color.holo_green_light));
                    Toast.makeText(context, "Rezervarea actuală.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("sendPosition");
                    intent.putExtra("position", 98);
                    localBroadcastManager.sendBroadcast(intent);
                    //holder.mTimeSlotDescription.setText("Disponibil");
                } else if (holder.card_time_slot.getTag() == Common.DISABLE_TAG && holder.mTimeSlotDescription.getText() == "Disponibil") {
                    holder.card_time_slot.setBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_red_light));
                    holder.mTimeSlotDescription.setText("Rezervarea mea");
                } else if (holder.card_time_slot.getCardBackgroundColor().getDefaultColor() == context.getResources()
                        .getColor(android.R.color.holo_green_light)) {
                    holder.card_time_slot.setBackgroundColor(context.getResources()
                            .getColor(android.R.color.holo_red_light));

                    for (TimesEditSlot slotValue : timeSlotList) {
                        int slot = Integer.parseInt(slotValue.getSlot().toString());
                        if (slot == Common.mySlot) {
                            for (CardView cardView : cardViewList) {
                                if (cardView.getCardBackgroundColor().getDefaultColor() == context.getResources()
                                        .getColor(android.R.color.holo_red_light) && cardView.getTag() == Common.DISABLE_TAG) {
                                    cardViewList.get(Common.mySlot.intValue()).setBackgroundColor(context.getResources()
                                            .getColor(android.R.color.holo_orange_light));
                                }
                            }
                            // holder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                        }
                    }

                    Intent intent = new Intent("sendPosition");
                    intent.putExtra("position", position);
                    localBroadcastManager.sendBroadcast(intent);
                }
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

        IRecyclerSelectedItemEditListener iRecyclerSelectedItemListener;

        public void setiRecyclerSelectedItemEditListener(IRecyclerSelectedItemEditListener iRecyclerSelectedItemListener) {
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
