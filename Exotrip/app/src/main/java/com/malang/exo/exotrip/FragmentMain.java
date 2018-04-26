package com.malang.exo.exotrip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMain extends Fragment {
    View view;
    ViewPager imagePager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        imagePager = (ViewPager) view.findViewById(R.id.imageViewPager);
        imagePager.setAdapter(new ImageViewAdapter(this.getActivity()));
        return view;
    }


    public FragmentMain() {
    }
}
