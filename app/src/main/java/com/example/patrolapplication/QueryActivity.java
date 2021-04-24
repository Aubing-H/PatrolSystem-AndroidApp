package com.example.patrolapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.patrolapplication.component.MyPagerAdapter;
import com.example.patrolapplication.component.TabFragment;
import com.google.android.material.tabs.TabLayout;

// QueryActivity必须继承FragmentActivity才能使用Fragment
public class QueryActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        if(frameLayout != null){
            if (savedInstanceState != null) {
                return;
            }
            TabFragment tabFragment = new TabFragment();
            tabFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(
                    R.id.fragment_container, tabFragment
            ).commit();
        }
    }
}
