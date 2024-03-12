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
public class GetRealTimeWeatherByLocationAsObject {

    public static void main(String[] args) {
        String requestURI = "http://localhost:8080/v1/realtime";
        String code = "SCL";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RealtimeWeather> realtimeWeatherResponseEntity =
                restTemplate.getForEntity(requestURI + "/" + code, RealtimeWeather.class);
        if (realtimeWeatherResponseEntity.getStatusCode() == HttpStatus.OK){
            log.info(realtimeWeatherResponseEntity.toString());
        }
    }
}
