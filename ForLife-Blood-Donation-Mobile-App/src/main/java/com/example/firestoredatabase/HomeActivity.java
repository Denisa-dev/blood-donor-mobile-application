package com.example.firestoredatabase;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.firestoredatabase.Adapter.TabsPagerAdapter;
import com.example.firestoredatabase.Model.Users;
import com.example.firestoredatabase.ProfileActivities.AccountActivity;
import com.example.firestoredatabase.dialogs.ConfirmOnBackPressed;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import static com.example.firestoredatabase.Model.NotificationChannel.CHANNEL_2_ID;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConfirmOnBackPressed.OnConfirmBackListener {

    private NotificationManagerCompat notificationManagerCompat;
    private DrawerLayout drawerLayout;
    private TextView nume, mEmail;
    public static Boolean flag;
    private Integer noBookings;
    public static Date lastDate, dateDonare;
    ViewPager viewPager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("Notifications");
    private CollectionReference userCollRef = db.collection("users");
    private DocumentReference userRef = db.document("users/" + mFirebaseAuth.getUid());
    private DocumentReference ui = FirebaseFirestore.getInstance()
            .collection("users")
            .document(mFirebaseAuth.getUid());
    private TabLayout tabLayout;

    private static String judet, grupa;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = findViewById(R.id.view_pager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        if (getIntent().getExtras() != null) {
            viewPager.setCurrentItem(1);
        } else
            viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(false);

        View nav_header = navigationView.getHeaderView(0);
        nume = nav_header.findViewById(R.id.name_text);
        mEmail = nav_header.findViewById(R.id.email_text);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ui.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

                if (documentSnapshot.exists()) {
                    Users user = documentSnapshot.toObject(Users.class);
                    String name = documentSnapshot.getString("nume");
                    String judet2 = documentSnapshot.getString("judet");
                    String sange = documentSnapshot.getString("grupaSange");
                    String tel = documentSnapshot.getString("telefon");
                    Date date = documentSnapshot.getDate("dataDonare");
                    user.setNume(name);
                    user.setJudet(judet2);
                    user.setGrupaSange(sange);
                    user.setTelefon(tel);
                    nume.setText(name);
                    user.setDataDonare(date);

                    noBookings = Integer.parseInt(documentSnapshot.get("noBookings").toString());
                    Common.noBook = noBookings;
                    if (noBookings > 0)
                        lastDate = documentSnapshot.getDate("dateBooking");
                    else
                        lastDate = documentSnapshot.getDate("dataDonare");
                    Calendar c2 = Calendar.getInstance();
                    if (noBookings == 0 || lastDate.before(c2.getTime())) {
                        flag = false;
                        Common.bookCheck = false;
                    } else {
                        flag = true;
                        Common.bookCheck = true;
                    }
                    Calendar c3 = Calendar.getInstance();
                    Calendar c4 = Calendar.getInstance();
                    c3.setTime(lastDate);
                    c3.add(Calendar.MONTH, 3);
                    c3.add(Calendar.DAY_OF_MONTH, -3);
                    if(Common.simpleFormatDate.format(c3.getTime()).equals(Common.simpleFormatDate.format(c4.getTime()))){
                        if(Common.getNotifyAllow){
                            sendOnChannel2();
                            Common.getNotifyAllow = false;
                        }
                    }

                    judet = user.getJudet();
                    grupa = user.getGrupaSange();
                    TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), judet, grupa);
                    viewPager.setAdapter(tabsPagerAdapter);
                    if (getIntent().getExtras() != null) {
                        viewPager.setCurrentItem(1);
                    } else
                        viewPager.setCurrentItem(0);
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
        });

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.exists()) {
                            Users user = queryDocumentSnapshots.toObject(Users.class);
                            String name = user.getNume();
                            String email = user.getEmail();
                            nume.setText(name);
                            mEmail.setText(email);
                        } else {
                            Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
                            startActivity(i);
                            finish();
                            //Toast.makeText(HomeActivity.this, "Autentificati-va!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "A apărut o eroare la intergoarea datelor.");
                    }
                });

        /*userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Users user = documentSnapshot.toObject(Users.class);
                    user.setGrupaSange(documentSnapshot.getString("grupaSange"));
                    user.setJudet(documentSnapshot.getString("judet"));
                    judet = user.getJudet();
                    grupa = user.getGrupaSange();

                    TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), judet, grupa);
                    viewPager.setAdapter(tabsPagerAdapter);
                    if (getIntent().getExtras() != null) {
                        viewPager.setCurrentItem(1);
                    } else
                        viewPager.setCurrentItem(0);
                    tabLayout.setupWithViewPager(viewPager);
                }

            }
        });*/
    }

    public void sendOnChannel2(){
        String title = "ForLife - Este timpul să donezi";
        String message = "Încă trei zile și poți dona din nou!";
        Notification notification = new NotificationCompat.Builder(this,
                CHANNEL_2_ID).setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.GREEN)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(2, notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_account:
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                finish();
                break;
            case R.id.nav_logout:
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(i);
                FirebaseAuth.getInstance().signOut();
                finish();
                Toast.makeText(HomeActivity.this, "Deconectare reușită.", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_bookings:
                ui.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        if (documentSnapshot.exists()) {
                            noBookings = Integer.parseInt(documentSnapshot.get("noBookings").toString());
                            if (noBookings > 0)
                                lastDate = documentSnapshot.getDate("dateBooking");
                            else
                                lastDate = documentSnapshot.getDate("dataDonare");
                            Calendar c2 = Calendar.getInstance();

                            if (noBookings == 0 || lastDate.before(c2.getTime())){// || lastDate.equals(c2.getTime())) {
                                flag = false;
                                Common.bookCheck = false;
                                startActivity(new Intent(HomeActivity.this, NoBookingActivity.class));
                                //finish();
                            } else {
                                flag = true;
                                Common.bookCheck = true;
                                startActivity(new Intent(HomeActivity.this, ActualBookActivity.class));
                                //finish();
                            }
                        }
                    }
                });
                break;
            case R.id.nav_history:
                startActivity(new Intent(HomeActivity.this, IstoricActivity.class));
                break;
            case R.id.nav_bonus:
                startActivity(new Intent(HomeActivity.this, BonusActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = getText(R.string.shareBody).toString();
                String shareMessage = getResources().getString(R.string.shareMessage);
                intent.putExtra(Intent.EXTRA_SUBJECT, shareMessage);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Alege prietenii din aplicații și dă share"));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ConfirmOnBackPressed onBackPressedDialog = new ConfirmOnBackPressed();
            onBackPressedDialog.show(getSupportFragmentManager(), "ConfirmBackPressDialog");
            //FirebaseAuth.getInstance().signOut();
            //finishAffinity();
            //super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Common.tab == 1) {
            viewPager.setCurrentItem(1);
            Common.tab = 0;
        } else if (Common.tab == 2) {
            viewPager.setCurrentItem(2);
            Common.tab = 0;
        } else if(Common.tab == 3) {
            viewPager.setCurrentItem(3);
            Common.tab = 0;
        }else
            viewPager.setCurrentItem(0);
    }

    @Override
    public void onConfirmBackListener(boolean flag) {
        if(flag == true){
            FirebaseAuth.getInstance().signOut();
            finishAffinity();
        }
    }
}
