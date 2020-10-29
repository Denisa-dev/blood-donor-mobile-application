package com.example.firestoredatabase.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.R;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView title, description;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title_notification);
        description = itemView.findViewById(R.id.descript_notification);
    }
}
