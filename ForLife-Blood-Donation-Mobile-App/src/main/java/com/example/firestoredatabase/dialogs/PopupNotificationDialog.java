package com.example.firestoredatabase.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.firestoredatabase.Common;
import com.example.firestoredatabase.R;

public class PopupNotificationDialog extends DialogFragment {

    ImageView img;
    TextView t1, t2, t3, t4;
    Button btn;
    private static String centru, titlu, desc, grupa, judet;
    private static final String TAG = "ConfirmLoadDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_notification,
                container, false);


        int width = 5;
        int height = 5;
        getDialog().getWindow().setLayout((int)(width * .20), (int)(height *.15));


        img = view.findViewById(R.id.close);
        t1 = view.findViewById(R.id.centru);
        t2 = view.findViewById(R.id.titlu);
        t3 = view.findViewById(R.id.descriere);
        t4 = view.findViewById(R.id.grupa);
        btn = view.findViewById(R.id.dona);

        Bundle mArgs = getArguments();
        centru = mArgs.getString("centru");
        titlu = mArgs.getString("titlu");
        desc = mArgs.getString("desc");
        grupa = mArgs.getString("grupa");
        judet = mArgs.getString("judet");

        Common.judetTab = judet;
        Common.centruTab = centru;

        t1.setText(centru);
        t2.setText(titlu);
        t3.setText(desc);
        t4.setText(grupa);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                Intent intent = new Intent();
                intent.putExtra("LOAD_FRAGMENT", "clicked");
                getTargetFragment().onActivityResult(
                        getTargetRequestCode(), Activity.RESULT_OK, intent
                );

            }
        });
        return view;
    }
}
