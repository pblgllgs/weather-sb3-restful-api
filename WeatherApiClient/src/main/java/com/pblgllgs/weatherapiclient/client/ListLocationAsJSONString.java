package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class ListLocationAsJSONString {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(requestURI, String.class);
            Map<String, String> responseObject = Map.of("data", response);
            log.info(responseObject.toString());
        } catch (RestClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            Map<String, Object> response = Map.of("error", ex.getMessage(), "status_code", statusCode);
            log.info(response.toString());
        }
    }
}
