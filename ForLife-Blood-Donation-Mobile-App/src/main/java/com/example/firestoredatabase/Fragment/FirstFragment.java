package com.example.firestoredatabase.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.firestoredatabase.Interfaces.PassDataFromFragmentToActivity;
import com.example.firestoredatabase.R;
import com.example.firestoredatabase.RegisterActivity;
import com.example.firestoredatabase.SignInActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.santalu.maskedittext.MaskEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    View view;
    Button next, previous;
    TextInputEditText name, varsta;
    MaskEditText phone;
    //CountryCodePicker ccp;

    String sex;
    String[] descriptionData = {"1", "2"};

    PassDataFromFragmentToActivity passDataFromFragmentToActivity;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first, container, false);
        name = view.findViewById(R.id.full_name_text);
        phone = view.findViewById(R.id.phoneText);
        varsta = view.findViewById(R.id.age);
        next = view.findViewById(R.id.button3);
        previous = view.findViewById(R.id.button4);

        StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sex, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        /*SecondFragment secondFragment = new SecondFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, secondFragment)
                .addToBackStack(null)
                .commit();*/

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (stateProgressBar.getCurrentStateNumber()) {
                    case 1:
                        if (name.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || varsta.getText().toString().isEmpty() || sex.isEmpty()) {
                            Toast.makeText(getContext(), "Completați câmpurile lipsă.", Toast.LENGTH_LONG).show();
                        } else {
                            RegisterActivity registerActivity = (RegisterActivity) getActivity();
                            if (name.getText().toString().length() <= 0 || phone.getText().toString().length() <= 0 ||
                                    varsta.getText().toString().length() <= 0 || sex.length() <= 0) {
                                Toast.makeText(getContext(), "Completați câmpurile lipsă.", Toast.LENGTH_LONG).show();
                                break;
                            } else {
                                if (varsta.getText().toString().startsWith("0")) {
                                    varsta.setError("Introduceți o vârstă validă!");
                                    varsta.requestFocus();
                                } else if (phone.getText().length() != 15) {
                                    phone.setError("Numărul de telefon nu este valid!");
                                    phone.requestFocus();
                                }
                                else if(!phone.getText().toString().matches("^\\+?(40)\\)?[ ]?([1-9]{3})[ ]?([0-9]{3})[ ]?([0-9]{3})$")){
                                    phone.setError("Numărul de telefon nu este valid!");
                                    phone.requestFocus();
                                }else {
                                    registerActivity.ReceiveData(name.getText().toString(), phone.getText().toString(), varsta.getText().toString(), sex);
                                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                    SecondFragment secondFragment = new SecondFragment();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.frame_layout, secondFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }

                            }
                        }
                        break;
                    case 2:
                        stateProgressBar.setAllStatesCompleted(true);
                        break;
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
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
        sex = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(adapterView.getContext(), sex, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
