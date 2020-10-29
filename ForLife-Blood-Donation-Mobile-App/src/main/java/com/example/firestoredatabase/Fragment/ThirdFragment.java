package com.example.firestoredatabase.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.firestoredatabase.R;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy");
    View view;
    Button next;
    EditText data;
    Spinner blood, judet;
    RadioGroup radioGroup;
    RadioButton radioF, radioM;
    String sex;
    String date;

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third, container, false);
        next =  (Button) view.findViewById(R.id.third_button);
        blood = view.findViewById(R.id.spinner_fragment);
        judet = view.findViewById(R.id.spinner_judet);
        data = view.findViewById(R.id.text_date);
        radioGroup = view.findViewById(R.id.radio_group);
        radioF = view.findViewById(R.id.radio_f);
        radioM = view.findViewById(R.id.radio_m);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_f){
                    sex = "F";
                }
                else
                    sex = "M";
            }
        });

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.blood));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blood.setAdapter(myAdapter);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.judet));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        judet.setAdapter(myAdapter2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = radioGroup.getCheckedRadioButtonId();
                if(id == radioF.getId())
                {
                    sex = "F";
                }
                else
                    sex = "M";
                date = data.getText().toString();
                /*RegisterActivity registerActivity = (RegisterActivity) getActivity();
                registerActivity.ReceiveData3(blood.getSelectedItem().toString(), date, sex, judet.getSelectedItem().toString());
                registerActivity.Save();*/
            }
        });
        return view;
    }
}
