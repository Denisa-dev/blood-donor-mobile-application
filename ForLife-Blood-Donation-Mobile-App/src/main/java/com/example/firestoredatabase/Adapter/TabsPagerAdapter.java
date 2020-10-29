package com.example.firestoredatabase.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.firestoredatabase.Fragment.CasesFragment;
import com.example.firestoredatabase.Fragment.ChatFragment;
import com.example.firestoredatabase.Fragment.HomeFragment;
import com.example.firestoredatabase.Fragment.NotificationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TabsPagerAdapter  extends FragmentStatePagerAdapter {

    String[] tabarray = new String[]{"Acasă", "Notificări", "Contact", "Cazuri"};
    Integer tabs = 4;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef = db.document("users/" + mFirebaseAuth.getUid());
    private  String judet, grupa;

    public TabsPagerAdapter(@NonNull FragmentManager fm, String judet, String grupa) {
        super(fm);
        this.judet = judet;
        this.grupa = grupa;
    }
    public TabsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabarray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new NotificationFragment(this.judet, this.grupa);
            case 2:
                return new ChatFragment();
            case 3:
                return new CasesFragment(this.grupa);
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabs;
    }
}
