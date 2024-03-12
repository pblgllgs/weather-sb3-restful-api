package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class GetHourlyWeatherByCodeAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/hourly/{code}";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-CURRENT-HOUR", "1");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestURI, HttpMethod.GET, httpEntity, String.class, Map.of("code","SCL"));
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.info("No data available for this location in this hours");
        }
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info(responseEntity.toString());
        }
    }
}
