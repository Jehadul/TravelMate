package com.jihad.app.travelmate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jihad.app.travelmate.R;
import com.jihad.app.travelmate.models.ForecastWeather;
import com.jihad.app.travelmate.weather.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ForecastWeatherAdapter extends RecyclerView.Adapter<ForecastWeatherAdapter.WeatherViewHolder> {

    private static List<ForecastWeather> mForecastWeatherList = new ArrayList<>();
    private LayoutInflater mInflater;
    protected Context context;

    public ForecastWeatherAdapter(Context context, List<ForecastWeather> forecastWeatherList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        mForecastWeatherList = forecastWeatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = mInflater.inflate(R.layout.card_view_weather_forecast_item, viewGroup, false);

        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int position) {

        ForecastWeather event = mForecastWeatherList.get(position);
        weatherViewHolder.setData(event);
    }

    @Override
    public int getItemCount() {
        return mForecastWeatherList.size();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView tvWeatherType, tvWeatherToday, tvWeatherTemp, tvWeatherPressure, tvWeatherWindSpeed, tvWeatherHumidity;
        private ImageView imgWeatherIcon;

        WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeatherType = itemView.findViewById(R.id.tv_forecast_weather_type);
            tvWeatherToday = itemView.findViewById(R.id.tv_forecast_weather_today);
            tvWeatherTemp = itemView.findViewById(R.id.tv_forecast_weather_temperature);
            tvWeatherPressure = itemView.findViewById(R.id.tv_forecast_weather_pressure);
            tvWeatherWindSpeed = itemView.findViewById(R.id.tv_forecast_weather_wind_speed);
            tvWeatherHumidity = itemView.findViewById(R.id.tv_forecast_weather_humidity);
            imgWeatherIcon = itemView.findViewById(R.id.iv_forecast_weather_icon);
        }

        public void setData(ForecastWeather weather) {

            Picasso.get().load(RetrofitHelper.getIconLinkFromName(weather.getIconUrl())).into(imgWeatherIcon);
            tvWeatherType.setText(weather.getType());
            tvWeatherToday.setText(weather.getDay());
            tvWeatherTemp.setText(String.valueOf(weather.getTemperature())+"\u00B0C");
            tvWeatherWindSpeed.setText(String.valueOf(weather.getWindSpeed())+"km/h");
            tvWeatherHumidity.setText(String.valueOf(weather.getHumidity()));
            tvWeatherPressure.setText(String.valueOf(weather.getPressure())+" mb");
        }
    }
}
