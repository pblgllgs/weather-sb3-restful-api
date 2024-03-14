package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullWeatherDTO {
    private String location;
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RealtimeWeatherFieldFilter.class)
    @JsonProperty("realtime_weather")
    private RealtimeWeatherDTO realtimeWeather = new RealtimeWeatherDTO();
    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> listHourlyWeather = new ArrayList<>();
    @JsonProperty("daily_forecast")
    private List<DailyWeatherDTO> listDailyWeather = new ArrayList<>();
}
