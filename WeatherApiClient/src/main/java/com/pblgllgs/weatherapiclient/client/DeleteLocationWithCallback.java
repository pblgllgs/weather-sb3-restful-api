package com.pblgllgs.weatherapiclient.client;
/*
 *
 * @author pblgl
 * Created on 11-03-2024
 *
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DeleteLocationWithCallback {
    public static void main(String[] args) {

        String requestURI = "http://localhost:8080/v1/locations/MADRID_ES";

        RestTemplate restTemplate = new RestTemplate();

        RequestCallback requestCallback = request -> log.info("Request uri: " + request.getURI());

        ResponseExtractor<Void> responseExtractor = response -> {
            HttpStatusCode statusCode = response.getStatusCode();
            log.info("Status code: " + statusCode);
            if (statusCode.is2xxSuccessful()) {
                log.info("location deleted successfully");
            }
            return null;
        };
        restTemplate.execute(requestURI, HttpMethod.DELETE, requestCallback, responseExtractor);
    }
}
