package com.malang.exo.exotrip;

import com.malang.exo.exotrip.Remote.IGoogleAPIService;
import com.malang.exo.exotrip.Remote.RetrofitClient;

public class Common {
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getRetrofit(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
