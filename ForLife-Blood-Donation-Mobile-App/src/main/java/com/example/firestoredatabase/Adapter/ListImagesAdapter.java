package com.example.firestoredatabase.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Model.ImagesAdapter;
import com.example.firestoredatabase.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;


public class ListImagesAdapter extends FirestoreRecyclerAdapter<ImagesAdapter, ListImagesAdapter.ViewHolder> {

    int[] flags = new int[]{
            R.drawable.heart,
            R.drawable.admin,
            R.drawable.blood,
            R.drawable.box,
            R.drawable.child,
            R.drawable.dona,
            R.drawable.sun,
            R.drawable.people,
            R.drawable.help,
            R.drawable.hs,
            R.drawable.veine,
            R.drawable.love,
            R.drawable.named,
            R.drawable.steto,
            R.drawable.tree,
            R.drawable.unnamed,
            R.drawable.another,
            R.drawable.download,
            R.drawable.birds,
            R.drawable.fly,
            R.drawable.groups,
            R.drawable.images,
            R.drawable.fingers,
            R.drawable.donate,
            R.drawable.globule,
            R.drawable.hands
    };


    public ListImagesAdapter(FirestoreRecyclerOptions<ImagesAdapter> options)
    {
        super(options);
    }

    private OnItemClickListener listener;

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull ImagesAdapter imagesAdapter) {
        setFadeAnimation(viewHolder.itemView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");

        ((ViewHolder) viewHolder).mTv_name.setText(imagesAdapter.getDate().substring(0, 2) + "/" + imagesAdapter.getDate().substring(3, 5) + "/"
        + imagesAdapter.getDate().substring(6));
        //Random r = new Random();
        //viewHolder.mImg.setImageResource(flags[r.nextInt(26) + 0]);
        viewHolder.mImg.setImageResource(flags[i]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history,
                parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTv_name;
        public ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTv_name = (TextView) itemView.findViewById(R.id.tvDate);
            mImg = (ImageView) itemView.findViewById(R.id.img_item);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener!= null){
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            }
        });
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
