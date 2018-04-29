package com.malang.exo.exotrip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentMain extends Fragment {
    View view;
    ViewPager imagePager;
    int currentIndext = 0;
    ImageViewAdapter imgAdp;

    RequestQueue requestQueue;
    List<SliderUtils> sliderImg;
    String requesUrl = "http://codetest.cobi.co.za/androids.json";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        sliderImg = new ArrayList<>();

        imagePager = (ViewPager) view.findViewById(R.id.imageViewPager);

        SendRequest();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimer(), 2000, 4000);

        return view;
    }
    public class MyTimer extends TimerTask
    {

        @Override
        public void run() {
            imgAdp= new ImageViewAdapter(sliderImg, getActivity());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentIndext = (currentIndext + 1) % imgAdp.getCount();
                    imagePager.setCurrentItem(currentIndext, true);
                }
            });

        }
    }

    public void SendRequest()
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, requesUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++)
                {
                    SliderUtils sliderUtils = new SliderUtils();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        sliderUtils.setSliderImageUrl(jsonObject.getString("image"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sliderImg.add(sliderUtils);

                }
                imgAdp = new ImageViewAdapter(sliderImg, getActivity());
                imagePager.setAdapter(imgAdp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public FragmentMain() {
    }
}
