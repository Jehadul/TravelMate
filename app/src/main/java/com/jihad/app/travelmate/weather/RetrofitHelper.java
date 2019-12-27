package com.jihad.app.travelmate.weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit;
    private static RetrofitHelper helper;

    private RetrofitHelper(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static WeatherService getService(){
        if(helper==null){
            helper = new RetrofitHelper();
        }
        return retrofit.create(WeatherService.class);
    }

    public static String getIconLinkFromName(String name){
        return String.format("https://openweathermap.org/img/w/%s.png",name);
    }

}
