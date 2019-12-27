package com.jihad.app.travelmate.models;

public class ForecastWeather {

    private String day;
    private String iconUrl;
    private String type;
    private double temperature;
    private int pressure;
    private double windSpeed;
    private int humidity;

    public ForecastWeather(String day, String iconUrl, String type, double temperature, int pressure, double windSpeed, int humidity) {
        this.day = day;
        this.iconUrl = iconUrl;
        this.type = type;
        this.temperature = temperature;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
    }

    public String getDay() {
        return day;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getType() {
        return type;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }
}
