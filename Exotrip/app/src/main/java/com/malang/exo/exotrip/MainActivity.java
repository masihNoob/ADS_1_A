package com.malang.exo.exotrip;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout_Id);
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarId);
        viewPager = (ViewPager)findViewById(R.id.viewPager_Id);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.AddFragmentList(new FragmentMain(), "Beranda");
        viewPagerAdapter.AddFragmentList(new FragmentExplore(), "Jelajahi");
        viewPagerAdapter.AddFragmentList(new FragmentAccount(), "Akun");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
