package com.pblgllgs.weatherapiclient.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HourlyWeather
        (@JsonProperty("hour_of_day") int hourOfDay,

         int temperature,
         int precipitation,

         String status) {

}
