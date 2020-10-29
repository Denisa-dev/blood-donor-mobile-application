package com.example.firestoredatabase.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.firestoredatabase.Fragment.Step1Fragment;
import com.example.firestoredatabase.Fragment.Step2Fragment;
import com.example.firestoredatabase.Fragment.Step3Fragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    public MyViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return Step1Fragment.getInstance();
            case 1:
                return Step2Fragment.getInstance();
            case 2:
                return Step3Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
