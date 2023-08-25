package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private final RealtimeWeatherService realtimeWeatherService;
    private final GeolocationService geolocationService;

    public RealtimeWeatherApiController(RealtimeWeatherService realtimeWeatherService, GeolocationService geolocationService) {
        this.realtimeWeatherService = realtimeWeatherService;
        this.geolocationService = geolocationService;
    }

    @GetMapping
    public ResponseEntity<Object> getRealtimeWeatherByIpAddress(HttpServletRequest request) throws GeolocationException, IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);

            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);

            return ResponseEntity.ok(realtimeWeather);
        } catch (GeolocationException e) {
            LOGGER.info(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }catch (LocationNotFoundException e) {
            LOGGER.info(e.getMessage(),e);
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping
//    public ResponseEntity<Object> getRealtimeWeatherByLocationCode(@PathVariable("code")String code) throws GeolocationException, IOException {
//        String ipAddress = CommonUtility.getIPAddress(request);
//        try {
//            Location locationFromIP = geolocationService.getLocation(ipAddress);
//
//            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
//
//            return ResponseEntity.ok(realtimeWeather);
//        } catch (GeolocationException e) {
//            LOGGER.info(e.getMessage(),e);
//            return ResponseEntity.badRequest().build();
//        }catch (LocationNotFoundException e) {
//            LOGGER.info(e.getMessage(),e);
//            return ResponseEntity.notFound().build();
//        }
//    }



}
