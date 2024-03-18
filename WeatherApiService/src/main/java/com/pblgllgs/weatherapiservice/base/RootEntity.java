package com.pblgllgs.weatherapiservice.base;
/*
 *
 * @author pblgl
 * Created on 18-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "locations_url",
        "location_by_code_url",
        "realtime_weather_by_ip_url",
        "realtime_weather_by_location_code_url",
        "hourly_forecast_by_ip_url",
        "hourly_forecast_by_location_code_url",
        "daily_forecast_by_ip_url",
        "daily_forecast_by_location_code_url",
        "full_weather_by_ip_url",
        "full_weather_by_location_code_url"
})
public class RootEntity {
    private String locationsUrl;
    private String locationByCodeUrl;
    private String realtimeWeatherByIpUrl;
    private String realtimeWeatherByLocationCodeUrl;
    private String hourlyForecastByIpUrl;
    private String hourlyForecastByLocationCodeUrl;
    private String dailyForecastByIpUrl;
    private String dailyForecastByLocationCodeUrl;
    private String fullWeatherByIpUrl;
    private String fullWeatherByLocationCodeUrl;
}

