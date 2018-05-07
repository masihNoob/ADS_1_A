package com.malang.exo.exotrip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malang.exo.exotrip.Model.MyPlaces;
import com.malang.exo.exotrip.Model.Results;
import com.malang.exo.exotrip.Remote.IGoogleAPIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentExplore extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final int MY_PERMISSION_CODE = 1000;
    View view;
    SupportMapFragment supportMapFragment;

    private static GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    private double latitude, longitude;
    private Location lastlocation;
    private Marker marker;
    private LocationRequest locationRequest;

    IGoogleAPIService iGoogleAPIService;
    MyPlaces currentPalce;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.explore_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //obtain the supportfragment and get notified when map ready
        supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if(supportMapFragment == null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapFragment, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        //init service
        iGoogleAPIService = Common.getGoogleAPIService();
        //request permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkLocationPermission();
    }

    private void nearbyPlace(final String placeType) {
        String url = getUrl(latitude, longitude, placeType);
        iGoogleAPIService.getNearbyPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {

                currentPalce = response.body();

                if(response.isSuccessful())
                {
                    for (int i =0; i < response.body().getResults().length;i++)
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlaces = response.body().getResults()[i];
                        double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());

                        String placeName = googlePlaces.getName();
                        String vicinity = googlePlaces.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        //store market by index
                        markerOptions.snippet(String.valueOf(i));

                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });

    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+1000);
        googlePlaceUrl.append("&type="+placeType);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl", googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    private boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        }
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                    }
                } else
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    public FragmentExplore() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap != null) {
            mMap.clear();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //when user select the marker, data will be stored into static variable
                Common.currentResult = currentPalce.getResults()[Integer.parseInt(marker.getSnippet())];
                //open new activity
                startActivity(new Intent(getActivity(), ViewPlace.class));
                return true;
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        if(marker != null) {
            marker.remove();
            mMap.clear();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if(googleApiClient != null) LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        nearbyPlace("tourism");
    }
}
