package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class AddLocationAsLocationObject {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        RestTemplate restTemplate = new RestTemplate();
        Location newLocation = new Location(
                "BA_ARG",
                "Buenos Aires",
                "Buenos Aires Capital",
                "Argentina",
                "AR",
                true
        );

        HttpEntity<Location> request = new HttpEntity<>(newLocation);
        ResponseEntity<Location> location = restTemplate.postForEntity(requestURI, request, Location.class);
        try {
            Map<String, Object> responseObject = Map.of("data", location);
            log.info(responseObject.toString());
        } catch (RestClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            Map<String, Object> responseError = Map.of("error", ex.getMessage(), "status_code", statusCode);
            log.info(responseError.toString());
        }
    }
}
