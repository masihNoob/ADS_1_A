package com.malang.exo.exotrip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentMain extends Fragment {
    View view;
    ViewPager imagePager;
    int currentIndext = 0;
    ImageViewAdapter imgAdp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        imagePager = (ViewPager) view.findViewById(R.id.imageViewPager);
        imagePager.setAdapter(new ImageViewAdapter(this.getActivity()));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimer(), 2000, 400);

        return view;
    }
    public class MyTimer extends TimerTask
    {

        @Override
        public void run() {
            currentIndext = (currentIndext + 1) % imgAdp.getCount();
            imagePager.setCurrentItem(currentIndext, true);
        }
    }

    public FragmentMain() {
    }
}
