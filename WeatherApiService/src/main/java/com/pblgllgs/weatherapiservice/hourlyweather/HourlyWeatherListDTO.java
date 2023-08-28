package com.pblgllgs.weatherapiservice.hourlyweather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherListDTO {
    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> hourlyForecast =  new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<HourlyWeatherDTO> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<HourlyWeatherDTO> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public void add(HourlyWeatherDTO hourlyWeatherDTO){
        this.hourlyForecast.add(hourlyWeatherDTO);
    }
}
