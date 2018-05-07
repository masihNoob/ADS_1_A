package com.malang.exo.exotrip;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    //RequestQueue requestQueue;
    //String requesUrl = "https://sikma.000webhostapp.com/androids.json";
    List<SliderUtils> sliderImg;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        //requestQueue = Volley.newRequestQueue(getActivity());

        sliderImg = new ArrayList<>();
        imagePager = (ViewPager) view.findViewById(R.id.imageViewPager);
        if(checkInternet()){
            initFirebase();
            addFirebaseEventListener();

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new MyTimer(), 2000, 4000);
        }else
        {
            Toast.makeText(getActivity(), "please check internet", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void addFirebaseEventListener() {
        databaseReference.child("versions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postdataSnapshot:dataSnapshot.getChildren())
                {
                    SliderUtils sliderUtils = new SliderUtils();
                    User user = postdataSnapshot.getValue(User.class);
                    sliderUtils.setSliderImageUrl(user.getImage());
                    sliderImg.add(sliderUtils);
                }
                imagePager.setAdapter(new ImageViewAdapter(sliderImg, getActivity()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public class MyTimer extends TimerTask
    {
        @Override
        public void run() {
            imgAdp= new ImageViewAdapter(sliderImg, getActivity());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(imgAdp.getCount() > 0) {
                        currentIndext = (currentIndext + 1) % imgAdp.getCount();
                        imagePager.setCurrentItem(currentIndext, true);
                    }else
                        Toast.makeText(getActivity(), "please wait", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
/*
volley (not used anymore)
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
*/
    public FragmentMain() {
    }
}
