package com.example.firestoredatabase.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.firestoredatabase.R;

public class ConfirmPasswordDialog extends DialogFragment {

    private static final String TAG = "ConfirmPasswordDialog";

    public interface  OnConfirmPasswordListener{
        public void onConfirmPasswordListener(String pass);
    }
    OnConfirmPasswordListener mOnConfirmPasswordListener;
    TextView pass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password,
                container, false);

        TextView cancelDialog = view.findViewById(R.id.cancel);
        pass = view.findViewById(R.id.editText2);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        TextView saveDialog = view.findViewById(R.id.save);
        saveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = pass.getText().toString();
                if(!password.equals("")) {
                    //Toast.makeText(getActivity(), password, Toast.LENGTH_SHORT).show();
                    mOnConfirmPasswordListener.onConfirmPasswordListener(password);
                    getDialog().dismiss();
                }else
                    Toast.makeText(getActivity(), "Parolă necompletată!", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "OnCreatedView started.");
        return view;
    }

    @Override
    public void onAttach(@NonNull Activity context) {
        super.onAttach(context);

        try {
            mOnConfirmPasswordListener = (OnConfirmPasswordListener)context;
        }catch (ClassCastException e){
            Log.d(TAG, e.getMessage());
        }
    }
}
