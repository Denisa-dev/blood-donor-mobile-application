package com.example.firestoredatabase.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.firestoredatabase.R;

public class ConfirmOnBackPressed extends DialogFragment {

    public interface  OnConfirmBackListener{
        public void onConfirmBackListener(boolean flag);
    }

    OnConfirmBackListener mOnConfirmBackListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_onback_pressed,
                container, false);

        Button yes, no;
        yes = view.findViewById(R.id.btn_yes);
        no = view.findViewById(R.id.btn_no);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnConfirmBackListener.onConfirmBackListener(true);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Activity context) {
        super.onAttach(context);

        try {
            mOnConfirmBackListener = (OnConfirmBackListener) context;
        }catch (ClassCastException e){
            Toast.makeText(getContext(), "Eroare!", Toast.LENGTH_LONG).show();
        }
    }
}

