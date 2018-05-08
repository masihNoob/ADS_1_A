package com.malang.exo.exotrip;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malang.exo.exotrip.Adapter.NearbyListAdapter;
import com.malang.exo.exotrip.Nearby.Item;

import java.util.ArrayList;
import java.util.List;

public class ListNearbyPlace extends Fragment {

    View view;

    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    List<Item> items = new ArrayList<>();

    public ListNearbyPlace() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_nearby_place, container, false);
        list = (RecyclerView)view.findViewById(R.id.recyclerV);
        list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        
        setData();
        return view;
    }

    private void setData() {
        
        NearbyListAdapter adapter = new NearbyListAdapter(items);
        list.setAdapter(adapter);
    }

}
