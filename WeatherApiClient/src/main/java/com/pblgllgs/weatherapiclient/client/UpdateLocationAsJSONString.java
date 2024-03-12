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

@Slf4j
public class UpdateLocationAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        RestTemplate restTemplate = new RestTemplate();

        String json = """
                {
                    "code": "MADRID_ES",
                    "enabled": true,
                    "city_name": "Madrid",
                    "region_name": "Comunidad de Madrid",
                    "country_name": "España",
                    "country_code": "ES"
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            restTemplate.put(requestURI, request, String.class);
            log.info("Location updated!");
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
