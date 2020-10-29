package com.example.firestoredatabase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.firestoredatabase.Adapter.AutoCompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FinalMapActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final float DEFAULT_ZOOM = 20f;
    private GoogleMap mMap, mMarkers;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int REQUEST_CODE = 99;
    private boolean isGPS = false;
    private ImageView search;
    private TextInputEditText mSearchLocation;
    AutoCompleteTextView autoCompleteTextView;
    TextView responseView;
    PlacesClient placesClient;
    AutoCompleteAdapter adapter2;
    private static String[] centres;

    double[] latCentre = {46.072429, 43.970355, 46.185215, 46.559505, 47.657865, 46.232082, 47.129716, 47.742121,
            45.651920, 45.269869, 45.150744, 44.206645, 46.776045, 44.186752, 44.306134, 44.919306,

            45.874790, 45.413720, 44.624703, 45.705186, 45.426403, 43.894884, 47.169801,

            46.354095, 47.064504, 46.926452, 44.864213, 45.275587, 44.942607, 45.290512, 45.107825,

            47.783475, 45.861604, 45.794554, 44.424677, 44.568162, 47.638786, 45.033065, 46.560774,
            45.737417, 45.177693, 47.195442, 44.453881, 44.453902, 44.464357, 44.442594, 44.436445};

    double[] lngCentre = {23.557411, 25.335071, 21.306891, 26.910769, 23.561020, 27.666821, 24.487210, 26.661595,
            25.599134, 27.969586, 26.798996, 27.318976, 23.597925, 28.641363, 23.791634, 25.457117,

            22.907132, 23.376331, 22.662504, 27.193274, 28.038816, 25.952604, 27.582568,

            25.809322, 21.947549, 26.373691, 24.861631, 25.046218, 25.992498, 21.885787, 24.367607,

            22.860491, 25.790211, 24.158959, 24.376160, 27.360068, 26.243036, 23.274336, 24.583024,
            21.239941, 28.782613, 23.050294, 26.079467, 26.101253, 26.156342, 26.072759, 26.071964};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_map);
        //mSearchLocation = findViewById(R.id.hint_search);
        search = findViewById(R.id.ic_magnify2);
        //responseView = findViewById(R.id.response);

        autoCompleteTextView = findViewById(R.id.auto);
        Context context = getApplicationContext();
        centres = context.getResources().getStringArray(R.array.centres);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_tex_color, R.id.text_Color, centres);
        autoCompleteTextView.setAdapter(adapter);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //searching
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, String title, Address address) {
        mMarkers.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {

            String snippet = "Locality: " + address.getLocality() + "\n" +
                    "ZIP Code: " + address.getPostalCode() + "\n";


            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMarkers.addMarker(options);
        }
    }

    private void moveCamera2(LatLng latLng, float zoom, String title, Address address) {
        mMarkers.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")) {

            String snippet = "Locality: " + address.getLocality() + "\n" +
                    "ZIP Code: " + address.getPostalCode() + "\n";


            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMarkers.addMarker(options);
        }
    }

    private void geoLocate() {
        String searchString = autoCompleteTextView.getText().toString();
        if (!searchString.isEmpty()) {
            Geocoder geocoder = new Geocoder(FinalMapActivity.this);
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(searchString, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list.size() > 0) {
                Address address = list.get(0);
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
                moveCamera2(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0), address);
                autoCompleteTextView.setText("");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.drop);
        Bitmap smallMark = Bitmap.createScaledBitmap(largeIcon, 50, 50, false);
        BitmapDescriptor myIcon = BitmapDescriptorFactory.fromBitmap(smallMark);
        mMap = googleMap;
        mMarkers = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            //mMarkers.setMyLocationEnabled(true);
        }

        Context context = getApplicationContext();
        String[] centreArray = context.getResources().getStringArray(R.array.centres);
        String[] addresses = context.getResources().getStringArray(R.array.addresses);

        // Add a marker in Sydney and move the camera
        for (int i = 0; i < latCentre.length; i++) {
            LatLng centre = new LatLng(latCentre[i], lngCentre[i]);
            Marker marker = mMap.addMarker(new MarkerOptions().position(centre).title(centreArray[i]).snippet(addresses[i])
                    .icon(myIcon));
            marker.setTag(centre);
        }

        LatLng RO = new LatLng(45.9852129, 24.6859225);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(6));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(RO));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String judet = Common.matchJudet(marker.getTitle());
                String centru = marker.getTitle();
                String adresa = marker.getSnippet();

                DocumentReference centreRef = FirebaseFirestore.getInstance()
                        .collection("donationLocation")
                        .document(judet)
                        .collection("NewBranch")
                        .document(centru);

                centreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Intent intent = new Intent(FinalMapActivity.this, LoadTimeMarkerActivity.class);
                                intent.putExtra("centru", centru);
                                intent.putExtra("judet", judet);
                                intent.putExtra("adresa", adresa);
                                startActivity(intent);
                                finish();
                            } else {
                                CollectionReference col = FirebaseFirestore.getInstance().collection("donationLocation");
                                col.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot dc : task.getResult()) {
                                                if (dc.getId().equals(judet)) {
                                                    //Toast.makeText(FinalMapActivity.this, judet, Toast.LENGTH_LONG).show();
                                                    CollectionReference doc = FirebaseFirestore.getInstance().collection("donationLocation")
                                                            .document(dc.getId()).collection("NewBranch");
                                                    doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            for (DocumentSnapshot myDoc : task.getResult()) {
                                                                if (myDoc.getId().compareTo(centru) != 0) {
                                                                    if (!adresa.contains("Locality")) {
                                                                        if (centru.contains("CTSM")) {
                                                                            if (myDoc.getId().contains("CTSM")) {
                                                                                Intent intent = new Intent(FinalMapActivity.this, LoadTimeMarkerActivity.class);
                                                                                intent.putExtra("centru", myDoc.getId());
                                                                                intent.putExtra("judet", dc.getId());
                                                                                intent.putExtra("adresa", adresa);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            //Toast.makeText(PopupNotificationActivity.this, myDoc.getId(), Toast.LENGTH_LONG).show();
                                                                            Intent intent = new Intent(FinalMapActivity.this, LoadTimeMarkerActivity.class);
                                                                            intent.putExtra("centru", myDoc.getId());
                                                                            //Toast.makeText(FinalMapActivity.this, myDoc.getId(), Toast.LENGTH_LONG).show();
                                                                            intent.putExtra("judet", dc.getId());
                                                                            intent.putExtra("adresa", adresa);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                            /*else {
                                Intent intent = new Intent(FinalMapActivity.this, ReservationActivity.class);
                                startActivity(intent);
                            }*/
                        }

                    }
                });
                return false;
            }
        });
    }

    public boolean checkUserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "A fost respins permisul pentru locație.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }
       /*LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(10));*/

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FinalMapActivity.this, HomeActivity.class));
        finish();
    }
}
