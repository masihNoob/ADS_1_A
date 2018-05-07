package com.malang.exo.exotrip;

import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.malang.exo.exotrip.Model.Photos;
import com.malang.exo.exotrip.Model.PlaceDetail;
import com.malang.exo.exotrip.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;
    RatingBar ratingBar;
    TextView openingHours, placeAddress, placeName;


    IGoogleAPIService iGoogleAPIService;
    PlaceDetail placeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        iGoogleAPIService = Common.getGoogleAPIService();

        photo = (ImageView)findViewById(R.id.placePhoto);
        ratingBar = (RatingBar)findViewById(R.id.placeRating);
        placeAddress = (TextView)findViewById(R.id.placeAddr);
        placeName = (TextView)findViewById(R.id.placeName);
        openingHours = (TextView)findViewById(R.id.placeOpenHour);

        placeName.setText("");
        placeAddress.setText("");
        openingHours.setText("");

        if(Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            Picasso.with(this).load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000)).placeholder(R.drawable.ic_image_black_24dp).error(R.drawable.ic_error_black_24dp).into(photo);
        }

        //set taring start
        if(Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else
        {
            ratingBar.setVisibility(View.GONE);
        }

        //openig hours
        if(Common.currentResult.getOpening_hours() != null)
        {
            openingHours.setText(Common.currentResult.getOpening_hours().getOpen_now());
        }
        else
        {
            openingHours.setVisibility(View.GONE);
        }

        //fetch address and name
        iGoogleAPIService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id())).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                placeDetail = response.body();
                placeAddress.setText(placeDetail.getResult().getFormatted_address());
                placeName.setText(placeDetail.getResult().getName());
            }

            @Override
            public void onFailure(Call<PlaceDetail> call, Throwable t) {

            }
        });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("placeid="+place_id);
        url.append("&key="+ getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photosRef, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photosRef);
        url.append("&key="+ getResources().getString(R.string.browser_key));

        return url.toString();
    }
}
