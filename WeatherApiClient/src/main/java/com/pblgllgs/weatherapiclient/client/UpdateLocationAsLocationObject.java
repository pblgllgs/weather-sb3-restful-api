package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import com.pblgllgs.weatherapiclient.client.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
public class UpdateLocationAsLocationObject {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        RestTemplate restTemplate = new RestTemplate();
        Location newLocation = new Location(
                "MADRID_ES",
                "Madrid",
                "Comunidad de Madrid",
                "España",
                "ES",
                true
        );
        HttpEntity<Location> request = new HttpEntity<>(newLocation);
        try {
            ResponseEntity<Location> location = restTemplate.exchange(
                    requestURI, HttpMethod.PUT, request, Location.class
            );
            if (location.getStatusCode().is2xxSuccessful()) {
                log.info("Update success: " + Objects.requireNonNull(location.getBody()));
            }
        } catch (RestClientResponseException ex) {
            log.error("Error: " + ex.getMessage());
        }
    }
}
