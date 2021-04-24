package com.example.patrolapplication.component;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.patrolapplication.R;
import com.google.android.material.tabs.TabLayout;

public class TabFragment extends Fragment {
    private static final String TAG = "TabFragment";

    private MyPagerAdapter myPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_frament, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        myPagerAdapter =  new MyPagerAdapter(getFragmentManager());
        viewPager = getView().findViewById(R.id.view_pager);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout = getView().findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}