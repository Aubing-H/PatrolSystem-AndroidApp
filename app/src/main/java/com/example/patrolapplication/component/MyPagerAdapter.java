package com.example.patrolapplication.component;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.patrolapplication.network.DataController;

import java.util.List;
import java.util.Map;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "MyPagerAdapter";

    public MyPagerAdapter(FragmentManager fmg){
        super(fmg, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new MyFragment(position);
        Bundle args = new Bundle();
        args.putInt(MyFragment.class.getName(), position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
