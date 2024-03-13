package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */
@RestController
@RequestMapping("/v1/daily")
@RequiredArgsConstructor
public class DailyWeatherApiController {

    private final DailyWeatherService dailyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException, IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        Location locationFromIp = geolocationService.getLocation(ipAddress);

        List<DailyWeather> dailyWeatherList = dailyWeatherService.getByLocation(locationFromIp);

        if (dailyWeatherList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(listEntityToDTO(dailyWeatherList));
    }

    private DailyWeatherListDTO listEntityToDTO(List<DailyWeather> dailyWeatherList) {
        Location location = dailyWeatherList.get(0).getId().getLocation();
        DailyWeatherListDTO dto = new DailyWeatherListDTO();
        dto.setLocation(location.toString());
        dailyWeatherList.forEach(dailyWeather -> {
            dto.addDailyWeatherDTO(modelMapper.map(dailyWeather,DailyWeatherDTO.class));
        });

        return dto;
    }
}
