package com.pblgllgs.weatherapiservice.base;
/*
 *
 * @author pblgl
 * Created on 18-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherApiController;
import com.pblgllgs.weatherapiservice.full.FullWeatherApiController;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherApiController;
import com.pblgllgs.weatherapiservice.location.LocationApiController;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherApiController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MainController {

    @GetMapping
    public ResponseEntity<RootEntity> handleBaseURI() throws GeolocationException, IOException {
        return ResponseEntity.ok(createRootEntity());
    }

    public RootEntity createRootEntity() throws GeolocationException, IOException {

        RootEntity entity = new RootEntity();

        String locationsUrl = linkTo(methodOn(LocationApiController.class).listLocation()).toString();
        entity.setLocationsUrl(locationsUrl);
        String locationByCodeUrl = linkTo(methodOn(LocationApiController.class).getLocation(null)).toString();
        entity.setLocationByCodeUrl(locationByCodeUrl);

        String realtimeByIp = linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIpAddress(null)).toString();
        entity.setRealtimeWeatherByIpUrl(realtimeByIp);
        String realtimeByLocationCode = linkTo(methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(null)).toString();
        entity.setRealtimeWeatherByLocationCodeUrl(realtimeByLocationCode);

        String hourlyByIp = linkTo(methodOn(HourlyWeatherApiController.class).findHourlyWeatherForecastByIPAddress(null)).toString();
        entity.setHourlyForecastByIpUrl(hourlyByIp);
        String hourlyByLocationCode = linkTo(methodOn(HourlyWeatherApiController.class).findHourlyWeatherForecastByLocationCode(null,null)).toString();
        entity.setHourlyForecastByLocationCodeUrl(hourlyByLocationCode);

        String dailyByIp = linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).toString();
        entity.setDailyForecastByIpUrl(dailyByIp);
        String dailyByLocationCode = linkTo(methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(null)).toString();
        entity.setDailyForecastByLocationCodeUrl(dailyByLocationCode);

        String fullByIp = linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).toString();
        entity.setFullWeatherByIpUrl(fullByIp);
        String fullByLocationCode = linkTo(methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(null)).toString();
        entity.setFullWeatherByLocationCodeUrl(fullByLocationCode);


        return entity;
    }
}
