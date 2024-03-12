package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetRealTimeWeatherByLocationAsJSONString {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/realtime/{code}";
        Map<String,String> params = new HashMap<>();
        params.put("code","SCL");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> location = restTemplate.getForEntity(requestURI,String.class, params);
        if (location.getStatusCode() == HttpStatus.OK){
            log.info(location.toString());
        }
    }
}
