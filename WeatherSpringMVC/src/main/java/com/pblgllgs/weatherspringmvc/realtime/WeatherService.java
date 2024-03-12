package com.pblgllgs.weatherspringmvc.realtime;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${api.weather.realtime.url}")
    private String getRealtimeWeatherRequestURI;

    private final RestTemplate restTemplate;

    public RealtimeWeather getRealtimeWeather(){
        try {
            return restTemplate.getForObject(getRealtimeWeatherRequestURI,RealtimeWeather.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new WeatherServiceException("Error calling realtime weather api: "+ e.getMessage());
        }
    }
}
