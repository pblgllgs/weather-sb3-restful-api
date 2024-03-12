package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class UpdateRealtimeAsLocationObject {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/realtime/{code}";
        RestTemplate restTemplate = new RestTemplate();
        RealtimeWeather realtimeWeather = new RealtimeWeather(
                50,
                20,
                25,
                30,
                null,
                "cloudy",
                null
        );
        HttpEntity<RealtimeWeather> request = new HttpEntity<>(realtimeWeather);
        try {
            ResponseEntity<RealtimeWeather> location = restTemplate.exchange(
                    requestURI, HttpMethod.PUT, request, RealtimeWeather.class, Map.of("code","SCL")
            );
            if (location.getStatusCode().is2xxSuccessful()) {
                log.info("Update success: " + Objects.requireNonNull(location.getBody()));
            }
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
