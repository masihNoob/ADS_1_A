package com.malang.exo.exotrip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class FragmentExplore extends Fragment implements OnMapReadyCallback {
    View view;
    SupportMapFragment supportMapFragment;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.explore_fragment, container, false);
        supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if(supportMapFragment == null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapFragment, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        return view;
    }

    public FragmentExplore() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
