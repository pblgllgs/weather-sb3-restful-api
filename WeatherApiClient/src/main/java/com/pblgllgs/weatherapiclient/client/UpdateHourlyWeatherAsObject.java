package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class UpdateHourlyWeatherAsObject {

    public static void main(String[] args) {

        String requestURI = "http://localhost:8080/v1/hourly/{code}";
        RestTemplate restTemplate = new RestTemplate();
        HourlyWeather hourlyWeather1 = new HourlyWeather(
                8, 11, 55, "cloudy"
        );
        HourlyWeather hourlyWeather2 = new HourlyWeather(
                9, 11, 50, "cloudy"
        );
        HourlyWeather hourlyWeather3 = new HourlyWeather(
                10, 11, 50, "cloudy"
        );
        List<HourlyWeather> hourlyWeatherList = List.of(hourlyWeather1, hourlyWeather2, hourlyWeather3);
        HttpEntity<Object> listHttpEntity = new HttpEntity<>(hourlyWeatherList);
        try {
            ResponseEntity<Object> responseEntity = restTemplate.exchange(
                    requestURI, HttpMethod.PUT, listHttpEntity, Object.class, Map.of("code","SCL")
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Update success: " + Objects.requireNonNull(responseEntity.getBody()));
            }
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
