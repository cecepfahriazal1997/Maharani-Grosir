package com.application.mgoaplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.application.mgoaplication.R;
import com.application.mgoaplication.activity.DashboardActivity;
import com.application.mgoaplication.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class TransactionFragment extends Fragment {
    private String title[] = {
            "Belum Diproses",
            "Diproses",
            "Selesai"
    };
    private DashboardActivity parent;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter adapter;
    public int tabIconColor ;
    public int tabIconColor2;
    private Map<String, String> param = new HashMap<>();

    public TransactionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent      = (DashboardActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView           = inflater.inflate(R.layout.fragment_transaction, container, false);

        findView(rootView);
        init();
        return rootView;
    }

    private void findView(View view) {
        viewPager               = view.findViewById(R.id.viewpager);
        tabLayout               = view.findViewById(R.id.tabs);
    }

    private void init() {
        tabIconColor            = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary);
        tabIconColor2           = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.darkGray);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(TransactionChildFragment.newInstance("0"), title[0]);
        adapter.addFragment(TransactionChildFragment.newInstance("1"), title[1]);
        adapter.addFragment(TransactionChildFragment.newInstance("2"), title[2]);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}