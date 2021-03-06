package com.malang.exo.exotrip;

import com.malang.exo.exotrip.Model.Results;
import com.malang.exo.exotrip.Remote.IGoogleAPIService;
import com.malang.exo.exotrip.Remote.RetrofitClient;
import com.malang.exo.exotrip.Remote.RetrofitScalarsClient;

public class Common {
    public static Results currentResult;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getRetrofit(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
    public static IGoogleAPIService getGoogleAPIServiceScalars()
    {
        return RetrofitScalarsClient.getScalarClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
