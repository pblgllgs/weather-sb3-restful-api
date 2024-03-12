package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record HourlyForecastDTO(
        String location,
        @JsonProperty("hourly_forecast")
        List<HourlyWeather> hourlyForecast
) {
}

