package com.application.mgoaplication.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.application.mgoaplication.R;
import com.application.mgoaplication.adapter.TabAdapter;
import com.application.mgoaplication.api.Service;
import com.application.mgoaplication.fragment.CategoryFragment;
import com.application.mgoaplication.fragment.DefaultFragment;
import com.application.mgoaplication.fragment.HomeFragment;
import com.application.mgoaplication.fragment.ProfileFragment;
import com.application.mgoaplication.fragment.TransactionFragment;
import com.application.mgoaplication.helper.GeneralHelper;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends MasterActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler;
    private boolean isPaused = false;
    public int tabIconColor ;
    public int tabIconColor2;
    private int[] tabIcons = {
            R.drawable.dashboard,
            R.drawable.category,
            R.drawable.transaction,
            R.drawable.username
    };
    public GeneralHelper generalHelper;
    public Service apiService;

    private String[] title = {
            "Beranda",
            "Kategori",
            "Transaksi",
            "Akun"
    };
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findView();
        initViewPopUpCart();
        init();
        helper.showPopUpCart(parent, popupCart, amount, price);
    }

    private void findView() {
        viewPager               = findViewById(R.id.viewpager);
        tabLayout               = findViewById(R.id.tabs);
    }

    private void init() {
        this.apiService         = service;
        this.generalHelper      = helper;
        tabIconColor            = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        tabIconColor2           = ContextCompat.getColor(getApplicationContext(), R.color.darkGray);

        setupViewPager(viewPager);
        helper.hideKeyboard();
        popupCart.setOnClickListener(this::onClick);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        changeColorIcon();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), title[0]);
        adapter.addFragment(new CategoryFragment(), title[1]);
        adapter.addFragment(new TransactionFragment(), title[2]);
        adapter.addFragment(new ProfileFragment(), title[3]);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabIcons.length; i++) {
            if (tabLayout.getTabAt(0).isSelected()) {
                setTabIcon(0, tabIcons[0], tabIconColor);
            }
            setTabIcon(i, tabIcons[i], tabIconColor2);
        }
    }

    private void changeColorIcon() {
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                   @Override
                   public void onTabSelected(TabLayout.Tab tab) {
                       super.onTabSelected(tab);
                       tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                   }

                   @Override
                   public void onTabUnselected(TabLayout.Tab tab) {
                       super.onTabUnselected(tab);
                       tab.getIcon().setColorFilter(tabIconColor2, PorterDuff.Mode.SRC_IN);
                   }

                   @Override
                   public void onTabReselected(TabLayout.Tab tab) {
                   }
               }
        );
    }

    public void setTabIcon(int pos, int icon, int color) {
        tabLayout.getTabAt(pos).setIcon(icon);
        if (color != 0)
            tabLayout.getTabAt(pos).getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        helper.showToast("Silahkan klik tombol kembali lagi untuk keluar Aplikasi !", 0);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
                handler.removeCallbacksAndMessages(null);
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popupCart:
                helper.startIntent(CartActivity.class, false, null);
                break;
        }
    }
}
