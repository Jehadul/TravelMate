package com.jihad.app.travelmate.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherService {

    @GET
    Call<WeatherResponse> getWeatherResponse(@Url String endUrl);
}
