package com.malang.exo.exotrip;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentMain extends Fragment {
    int INTERNET_PERMISSION_CODE = 1;
    View view;
    ViewPager imagePager;
    TextView idText;
    int currentIndext = 0;
    ImageViewAdapter imgAdp;

    RequestQueue requestQueue;
    List<SliderUtils> sliderImg;
    String requesUrl = "https://exotrip-c3f36.firebaseio.com";

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        idText = view.findViewById(R.id.idText);
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
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requesUrl,
               null,
               new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           JSONArray jsonArray = response.getJSONArray("versions");
                           for (int i = 0; i < jsonArray.length(); i++)
                           {
                               SliderUtils sliderUtils = new SliderUtils();
                               JSONObject jsonObject = jsonArray.getJSONObject(i);
                               //idText.setText(jsonObject.getString("name"));
                               sliderUtils.setSliderImageUrl(jsonObject.getString("image"));
                               sliderImg.add(sliderUtils);
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       imagePager.setAdapter(new ImageViewAdapter(sliderImg, getActivity()));
                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.e("Volley", "error");
                   }
               }
       );
       requestQueue.add(jsonObjectRequest);

    }

    public FragmentMain() {
    }
}
