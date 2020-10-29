package com.example.firestoredatabase;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class CustomMapActivity extends AppCompatActivity {

    private static final String TAG = "CustomMapActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_map);

        if(isServicesOk()){
            init();
        }
    }

    private void init(){
        Button mMap = findViewById(R.id.btn_map);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomMapActivity.this, MapInitActivity.class));
            }
        });
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServiceOk: chechking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CustomMapActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOk: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOk: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CustomMapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Nu puteți accesa harta!", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
