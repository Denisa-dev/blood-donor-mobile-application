package com.example.firestoredatabase.Fragment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.firestoredatabase.R;
import com.example.firestoredatabase.RegisterActivity;
import com.example.firestoredatabase.SignInActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final  String TAG = "SecondFragment";
    private static String date, judet, blood;
    private static Integer an, luna, zi;

    String[] descriptionData = {"1", "2"};
    private DatePickerDialog.OnDateSetListener mDateListener;

    View view;
    Button next, drop;
    TextInputEditText tvDate;
    CheckBox checkBox;
    TextInputLayout inputLayout;
    TextInputEditText weight;
    com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_second, container, false);
        tvDate = view.findViewById(R.id.tvDate);
        next = view.findViewById(R.id.button3);
        checkBox = view.findViewById(R.id.checkBox);
        inputLayout = view.findViewById(R.id.tvSelectDate);
        drop = view.findViewById(R.id.button4);
        weight = view.findViewById(R.id.weight);

        StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.judet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Spinner spinner2 = view.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.blood, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        inputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month =  calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);

                DatePickerDialog pickerDialog = new DatePickerDialog(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateListener, year, month, day);
                pickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pickerDialog.show();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tvDate.setEnabled(false);
                    inputLayout.setEnabled(false);
                    inputLayout.setBackgroundColor(getResources().getColor(R.color.white));
                }
                else
                {
                    tvDate.setEnabled(true);
                    inputLayout.setEnabled(true);
                    inputLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);

                DatePickerDialog pickerDialog = new DatePickerDialog(
                        getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateListener, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                pickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pickerDialog.show();
            }
        });

        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = day + "_" + month + "_" + year;
                String date2 = day + "/" + month + "/" + year;
                tvDate.setText(date2);
                an = year;
                zi = day;
                luna = month;
                checkBox.setEnabled(false);
            }
        };

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blood = spinner2.getSelectedItem().toString();
                if(blood == null || judet == null  || weight == null)
                {
                    Toast.makeText(getContext(), "Nu ați completat câmpurile obligatorii.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(checkBox.isChecked() && (date == null || date != null))
                    {
                        date = "10_10_2010";
                        RegisterActivity registerActivity = (RegisterActivity) getActivity();
                        if(judet.length() <= 0 || blood.length() <= 0 || date.length() <= 0 || weight.length() <= 0) {
                            Toast.makeText(getContext(), "Completați câmpurile lipsă.", Toast.LENGTH_LONG).show();
                        }
                        else if(weight.getText().toString().startsWith("0")){
                            weight.setError("Introduceți o greutate validă!");
                            weight.requestFocus();
                        }
                        else
                        {
                            registerActivity.ReceiveData2(judet, blood, date, Integer.parseInt(weight.getText().toString()));
                            registerActivity.Save();
                        }
                    }
                    else if(checkBox.isChecked() == false && date != null){
                        RegisterActivity registerActivity = (RegisterActivity) getActivity();
                        if(judet.length() <= 0 || blood.length() <= 0 || date.length() <= 0 || weight.length() <= 0) {
                            Toast.makeText(getContext(), "Completați câmpurile lipsă.", Toast.LENGTH_LONG).show();
                        }
                        else if(weight.getText().toString().startsWith("0")){
                            weight.setError("Introduceți o greutate validă!");
                            weight.requestFocus();
                        }
                        else
                        {
                            registerActivity.ReceiveData2(judet, blood, date, Integer.parseInt(weight.getText().toString()));
                            registerActivity.Save();
                        }
                    }
                    else if(date == null && checkBox.isChecked() == false)
                    {
                        Toast.makeText(getContext(), "Nu ați completat câmpurile afișate.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), SignInActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        judet = adapterView.getItemAtPosition(i).toString();
        //blood = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(), judet + blood, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
