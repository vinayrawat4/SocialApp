package com.vinay.socialapp.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.vinay.socialapp.Adapter.ViewPagerAdapter;
import com.vinay.socialapp.R;


public class NotoficationsFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    public NotoficationsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notofications, container, false);

        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        tabLayout= view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}