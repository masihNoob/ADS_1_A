package com.malang.exo.exotrip;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.malang.exo.exotrip.Helper.DirectionJSONParser;
import com.malang.exo.exotrip.Model.PlaceDetail;
import com.malang.exo.exotrip.Remote.IGoogleAPIService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDirection extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    Location lastlocation;
    Marker currentMarker;

    Polyline polyline;

    IGoogleAPIService iGoogleAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_direction);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        iGoogleAPIService = Common.getGoogleAPIServiceScalars();

        buildLocationCallBack();
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lastlocation = locationResult.getLastLocation();

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()))
                        .title("your location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                currentMarker = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                //marker for destination
                LatLng destinationLatLng = new LatLng(Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLat()),
                        Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLng()));
                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(Common.currentResult.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                drawPath(lastlocation, Common.currentResult.getGeometry().getLocation());
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lastlocation = location;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()))
                        .title("your location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                currentMarker = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                //marker for destination
                LatLng destinationLatLng = new LatLng(Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLat()),
                        Double.parseDouble(Common.currentResult.getGeometry().getLocation().getLng()));
                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(Common.currentResult.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                drawPath(lastlocation, Common.currentResult.getGeometry().getLocation());
            }
        });
    }

    private void drawPath(Location lastlocation, com.malang.exo.exotrip.Model.Location location) {
        if(polyline != null) polyline.remove();

        String origin = new StringBuilder(String.valueOf(lastlocation.getLatitude())).append(",").append(String.valueOf(lastlocation.getLongitude())).toString();
        String destination= new StringBuilder(location.getLat()).append(",").append(location.getLng()).toString();

        iGoogleAPIService.getDirections(origin, destination).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                new ParserTask().execute(response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{
        AlertDialog waitDialog = new SpotsDialog(ViewDirection.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog.show();
            waitDialog.setMessage("please wait, processing your request");

        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for(int i = 0; i < lists.size();i++)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);

                    Double lat = Double.parseDouble(point.get("lat"));
                    Double lng = Double.parseDouble(point.get("lng"));

                    LatLng positions = new LatLng(lat, lng);

                    points.add(positions);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            polyline = mMap.addPolyline(polylineOptions);
            waitDialog.dismiss();
        }
    }
}
