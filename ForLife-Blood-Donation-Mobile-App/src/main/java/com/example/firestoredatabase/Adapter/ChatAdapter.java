package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Model.EmailContactList;
import com.example.firestoredatabase.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    List<EmailContactList> list;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public ChatAdapter(List<EmailContactList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,
                parent, false);
        final ChatHolder viewHolder = new ChatHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        EmailContactList cards = list.get(position);
        final String centru = cards.getCentru();
        final String judet = cards.getJudet();

        holder.user_name.setText(centru);
        holder.user_bookings.setText(judet);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        TextView user_bookings;

        public ChatHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            user_name = itemView.findViewById(R.id.centru);
            user_bookings = itemView.findViewById(R.id.judet);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
