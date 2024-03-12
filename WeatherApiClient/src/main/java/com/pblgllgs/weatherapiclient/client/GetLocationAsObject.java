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
import org.springframework.web.client.RestTemplate;

@Slf4j
public class GetLocationAsObject {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/locations";
        String code = "NYC_US";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Location> location = restTemplate.getForEntity(requestURI + "/" + code, Location.class);
        if (location.getStatusCode() == HttpStatus.OK){
            log.info(location.toString());
        }
    }
}
