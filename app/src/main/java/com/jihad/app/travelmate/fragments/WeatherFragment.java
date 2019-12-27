package com.jihad.app.travelmate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.adapters.ForecastWeatherAdapter;
import com.jihad.app.travelmate.app.MainActivity;
import com.jihad.app.travelmate.common.Functions;
import com.jihad.app.travelmate.models.ForecastWeather;
import com.jihad.app.travelmate.weather.RetrofitHelper;
import com.jihad.app.travelmate.weather.WeatherResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherFragment extends Fragment {

    private MainActivity mainActivity;
    private Context context;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    private FusedLocationProviderClient providerClient;

    private ImageView imgWeatherIcon;
    private TextView tvWeatherType, tvWeatherToday, tvWeatherTemp, tvWeatherLocality, tvWeatherWindSpeed, tvWeatherHumidity, tvWeatherPressure, tvWeatherVisibility, tvError;

    //---------------------------------------
    private String icon, type;
    private int humidity, pressure;
    private double temp, windSpeed;

    //----------forecast weather-------
    private RecyclerView recyclerForecastWeatherView;
    protected ForecastWeatherAdapter weatherAdapter;
    protected LinearLayoutManager linearLayoutManager;
    private List<ForecastWeather> mForecastWeatherList = new ArrayList<>();


    public WeatherFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        imgWeatherIcon      = view.findViewById(R.id.iv_current_weather_icon);
        tvWeatherType       = view.findViewById(R.id.tv_current_weather_type);
        tvWeatherTemp       = view.findViewById(R.id.tv_current_weather_temp);
        tvWeatherToday      = view.findViewById(R.id.tv_current_weather_today);
        tvWeatherLocality   = view.findViewById(R.id.tv_current_weather_locality);

        tvWeatherWindSpeed  = view.findViewById(R.id.tv_current_weather_wind_speed);
        tvWeatherHumidity   = view.findViewById(R.id.tv_current_weather_humidity);
        tvWeatherPressure   = view.findViewById(R.id.tv_current_weather_pressure);
        tvWeatherVisibility = view.findViewById(R.id.tv_current_weather_visibility);
        tvError             = view.findViewById(R.id.tv_forecast_weather_error);

        recyclerForecastWeatherView = view.findViewById(R.id.recycler_weather_forcast_list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mainActivity.getSupportActionBar().setTitle("Weather");

        providerClient = LocationServices.getFusedLocationProviderClient(context);

        getDeviceLastLocation();

    }

    private void setUpRecyclerEventView() {


        mForecastWeatherList.clear();
        for(int i=1; i<6; i++){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_WEEK, i);

            String day = Functions.printDateFormat(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

            mForecastWeatherList.add(new ForecastWeather(day, icon, type, temp, pressure, windSpeed, humidity));
        }

        if (mForecastWeatherList.size()<1){
            tvError.setVisibility(View.VISIBLE);
        } else {
            tvError.setVisibility(View.INVISIBLE);
        }

        weatherAdapter = new ForecastWeatherAdapter(context, mForecastWeatherList);
        recyclerForecastWeatherView.setAdapter(weatherAdapter);

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerForecastWeatherView.setLayoutManager(linearLayoutManager);
        recyclerForecastWeatherView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean checkLocationPermission(){

         int accessLocation = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessLocation != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);

            return false;
        }
        return true;
    }

    private void getDeviceLastLocation(){

        if(checkLocationPermission()){

            providerClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            double latitude;
                            double longitude;

                            if(location == null){
                                latitude = 23.780417;
                                longitude = 90.378346;

                                Toast.makeText(context, "Accurate location not found", Toast.LENGTH_SHORT).show();
                            }else {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                            setLatLng(latitude, longitude);
                        }
                    });
        }
    }

    public void setLatLng(double lat, double lng){
        String unit = "metric";
        String apiKey = getString(R.string.weather_api_key);
        String endUrl = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", lat, lng, unit, apiKey);

        RetrofitHelper.getService().getWeatherResponse(endUrl)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if(response.isSuccessful()){
                            WeatherResponse weatherResponse = response.body();

                            icon = weatherResponse.getWeather().get(0).getIcon();
                            type = weatherResponse.getWeather().get(0).getMain();

                            temp = weatherResponse.getMain().getTemp();
                            String city = weatherResponse.getName();
                            String country = weatherResponse.getSys().getCountry();

                            windSpeed = weatherResponse.getWind().getSpeed();
                            humidity = weatherResponse.getMain().getHumidity();

                            pressure = weatherResponse.getMain().getPressure();
                            int visibility = weatherResponse.getVisibility();

                            Picasso.get().load(RetrofitHelper.getIconLinkFromName(icon)).into(imgWeatherIcon);
                            tvWeatherType.setText(type);
                            tvWeatherToday.setText(Functions.getTodayName()+" "+Functions.currentDateFormat());
                            tvWeatherTemp.setText(String.valueOf(temp)+"\u00B0C");

                            tvWeatherLocality.setText(city+", "+country);

                            tvWeatherWindSpeed.setText(String.valueOf(windSpeed)+"km/h");
                            tvWeatherHumidity.setText(String.valueOf(humidity));
                            tvWeatherPressure.setText(String.valueOf(pressure)+" mb");
                            tvWeatherVisibility.setText(String.valueOf(visibility));

                            setUpRecyclerEventView();

                        }else {
                            Toast.makeText(mainActivity, "error:" + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        Log.e("xxxxxx", "onFailure: " + t.getMessage() );
                    }
                });
    }

}
