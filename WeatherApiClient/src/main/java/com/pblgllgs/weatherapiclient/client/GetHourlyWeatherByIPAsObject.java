package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class GetHourlyWeatherByIPAsObject {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/hourly";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.add("X-CURRENT-HOUR", "7");
        headers.add("X-FORWARDED-FOR","101.46.168.0");

        HttpEntity<HourlyForecastDTO> httpEntity = new HttpEntity<HourlyForecastDTO>(headers);
        ResponseEntity<HourlyForecastDTO> responseEntity =
                restTemplate.exchange(requestURI, HttpMethod.GET, httpEntity, HourlyForecastDTO.class);
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("No hourly forecast available for this location in this hours");
        }
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            HourlyForecastDTO hourlyForecastDTO = responseEntity.getBody();
            assert hourlyForecastDTO != null;
            log.info("Hourly forecast for: {}",hourlyForecastDTO.location());
            hourlyForecastDTO.hourlyForecast().forEach(hourlyWeather -> log.info(hourlyWeather.toString()));
        }
    }
}
