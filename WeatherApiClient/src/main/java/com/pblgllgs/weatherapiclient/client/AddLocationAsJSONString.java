package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class AddLocationAsJSONString {
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

        ResponseEntity<String> response = restTemplate.postForEntity(requestURI, request, String.class);
        try {
            Map<String, Object> responseObject = Map.of("data", response);
            log.info(responseObject.toString());
        } catch (RestClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            Map<String, Object> responseError = Map.of("error", ex.getMessage(), "status_code", statusCode);
            log.info(responseError.toString());
        }
    }
}
