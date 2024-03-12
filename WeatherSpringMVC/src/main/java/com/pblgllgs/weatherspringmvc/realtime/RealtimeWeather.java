package com.pblgllgs.weatherspringmvc.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


public record RealtimeWeather(
    int temperature,
    int humidity,
    int precipitation,
    @JsonProperty("wind_speed")
    int windSpeed,
    @JsonProperty("last_updated")
    Date lastUpdated,
    String status,
    String location){
}
