package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class UpdateRealtimeAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/realtime/{code}";
        RestTemplate restTemplate = new RestTemplate();

        String json = """
                {
                    "temperature":50,
                    "humidity":55,
                    "precipitation":25,
                    "status":"Cloudy",
                    "wind_speed":30
                }
                """;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json,httpHeaders);

        try {
            restTemplate.put(requestURI, requestEntity, Map.of("code","SCL"));
            log.info("Location updated!");
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
