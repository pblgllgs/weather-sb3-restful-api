package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DeleteLocationAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations/{code}";
        Map<String,String> params = new HashMap<>();
        params.put("code","MADRID_E");
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.delete(requestURI, params);
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e);
        }

    }
}
