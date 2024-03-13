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

@Slf4j
public class GetRealTimeWeatherByIPAddressAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/realtime";
        String ip = "101.46.168.0";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-FORWARDED-FOR", ip);
        HttpEntity<String> requestHeaders = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> location = restTemplate.exchange(requestURI, HttpMethod.GET, requestHeaders, String.class);
        if (location.getStatusCode() == HttpStatus.OK) {
            log.info(location.toString());
        }
    }
}
