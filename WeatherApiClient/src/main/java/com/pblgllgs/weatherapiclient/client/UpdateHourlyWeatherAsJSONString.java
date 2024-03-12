package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class UpdateHourlyWeatherAsJSONString {

    public static void main(String[] args) {

        String requestURI = "http://localhost:8080/v1/hourly/SCL";
        RestTemplate restTemplate = new RestTemplate();
        String json = """
                [
                    {
                        "temperature": 15,
                        "precipitation": 55,
                        "status": "Cloudy",
                        "hour_of_day": 8
                    },
                    {
                        "temperature": 15,
                        "precipitation": 50,
                        "status": "Cloudy",
                        "hour_of_day": 9
                    },
                    {
                        "temperature": 16,
                        "precipitation": 40,
                        "status": "Cloudy",
                        "hour_of_day": 10
                    }
                ]
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            restTemplate.put(requestURI, request, String.class);
            log.info("Hourly updated!");
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
