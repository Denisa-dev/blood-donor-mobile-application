package com.example.firestoredatabase.Adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firestoredatabase.Model.Notification;
import com.example.firestoredatabase.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationHolder> {

    private static Boolean myFlag;
    private int currentPosition =-1;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.YYYY");
    Calendar calendar = Calendar.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("Notifications");
    private FirestoreRecyclerOptions<Notification> options;
    private static String judet, grupa;
    private OnItemClickListener listener;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options, Boolean flag, String judet, String grupa) {
        super(options);
        setHasStableIds(true);
        this.options = options;
        this.judet = judet;
        this.grupa = grupa;
        myFlag = flag;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public Notification getItem(int position) {
        return super.getItem(position);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder notificationHolder, int i, @NonNull Notification notification) {

        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) notificationHolder.cardView.getLayoutParams();
        notificationHolder.setIsRecyclable(false);

        if(myFlag == false){
            if (notification.getGrupa().equals(this.grupa)) {
                notificationHolder.title.setText(notification.getTitlu());
                notificationHolder.description.setText(notification.getDescriere());
                if(sdf2.format(notification.getData()).equals(sdf2.format(calendar.getTime()))){
                    notificationHolder.ora.setText(sdf.format(notification.getData()));
                    notificationHolder.time.setVisibility(View.GONE);
                }
                else {
                    notificationHolder.time.setText(sdf2.format(notification.getData()));
                    notificationHolder.ora.setVisibility(View.GONE);
                }
                layoutParams.setMargins(0, 0, 0, 8);
                notificationHolder.cardView.requestLayout();
            } else {
                notificationHolder.rl.setVisibility(View.GONE);
                layoutParams.setMargins(0, 0, 0, 0);
                notificationHolder.cardView.requestLayout();
            }
        } else if (myFlag == true) {
            if (notification.getGrupa().equals(this.grupa) && notification.getJudet().equals(this.judet)) {
                notificationHolder.title.setText(notification.getTitlu());
                if(sdf2.format(notification.getData()).equals(sdf2.format(calendar.getTime()))){
                    notificationHolder.ora.setText(sdf.format(notification.getData()));
                    notificationHolder.time.setVisibility(View.GONE);}
                else {
                    notificationHolder.time.setText(sdf2.format(notification.getData()));
                    notificationHolder.ora.setVisibility(View.GONE);
                }
                notificationHolder.description.setText(notification.getDescriere());
                layoutParams.setMargins(0, 0, 0, 8);
                notificationHolder.cardView.requestLayout();
            } else {
                notificationHolder.rl.setVisibility(View.GONE);
                layoutParams.setMargins(0, 0, 0, 0);
                notificationHolder.cardView.requestLayout();
            }
        }
    }


    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,
                parent, false);

        return new NotificationHolder(v);
    }


    class NotificationHolder extends RecyclerView.ViewHolder {

        TextView title, time, ora;
        TextView description;
        RelativeLayout rl;
        CardView cardView;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            rl = itemView.findViewById(R.id.notifyItem);
            cardView = itemView.findViewById(R.id.cardNotify);
            title = itemView.findViewById(R.id.title_notification);
            time = itemView.findViewById(R.id.time);
            ora = itemView.findViewById(R.id.ora);
            description = itemView.findViewById(R.id.descript_notification);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener!= null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        title.setTypeface(null, Typeface.NORMAL);
                        description.setTypeface(null, Typeface.NORMAL);
                        ora.setTypeface(null, Typeface.NORMAL);
                        time.setTypeface(null, Typeface.NORMAL);
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
