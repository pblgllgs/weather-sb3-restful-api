package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class ListLocationAsLocationObject {
    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        RestTemplate restTemplate = new RestTemplate();
        try {
            List<Location> response = restTemplate.getForObject(requestURI, List.class);
            assert response != null;
            response.forEach(resp -> log.info(resp.toString()));
        } catch (HttpClientErrorException | UnknownHttpStatusCodeException | HttpServerErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            Map<String, Object> response = Map.of("error", ex.getMessage(), "status_code", statusCode);
            log.info(response.toString());
        }
    }
}
