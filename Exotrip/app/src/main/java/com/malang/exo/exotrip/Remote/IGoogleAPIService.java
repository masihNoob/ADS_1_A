package com.malang.exo.exotrip.Remote;

import com.malang.exo.exotrip.Model.MyPlaces;
import com.malang.exo.exotrip.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}
