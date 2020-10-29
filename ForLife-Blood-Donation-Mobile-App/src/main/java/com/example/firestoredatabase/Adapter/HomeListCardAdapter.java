package com.example.firestoredatabase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.AboutDonationActivity;
import com.example.firestoredatabase.DonationBenefitActivity;
import com.example.firestoredatabase.FinalMapActivity;
import com.example.firestoredatabase.HappyCasesActivity;
import com.example.firestoredatabase.Model.HomeListCards;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.ReservationActivity;
import com.example.firestoredatabase.StatisticActivity;

import java.util.List;

public class HomeListCardAdapter extends RecyclerView.Adapter<HomeListCardAdapter.ViewHolder> {

    List<HomeListCards> list;
    Context context;

    public HomeListCardAdapter(List<HomeListCards> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cards, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.card_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewHolder.card_title.getText().toString().toLowerCase()) {
                    case "vreau să donez":
                        context.startActivity(new Intent(context, ReservationActivity.class));
                        break;
                    case "caută-ne pe hartă":
                        context.startActivity(new Intent(context, FinalMapActivity.class));
                        break;
                    case "despre donare":
                        context.startActivity(new Intent(context, AboutDonationActivity.class));
                        break;
                    case "tu donezi, noi bonificăm":
                        context.startActivity(new Intent(context, DonationBenefitActivity.class));
                        break;
                    case "vieți salvate":
                        context.startActivity(new Intent(context, HappyCasesActivity.class));
                        break;
                    case "statistici":
                        context.startActivity(new Intent(context, StatisticActivity.class));
                        break;
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HomeListCards cards = list.get(position);
        final int img = cards.getImg();
        final String category = cards.getCategory();

        holder.card_img.setImageResource(img);
        holder.card_title.setText(category);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView card_img;
        TextView card_title;
        LinearLayout card_home;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card_img = (ImageView) itemView.findViewById(R.id.card_img);
            card_title = (TextView) itemView.findViewById(R.id.card_title);
            card_home = (LinearLayout) itemView.findViewById(R.id.card_home);
        }
    }
}
