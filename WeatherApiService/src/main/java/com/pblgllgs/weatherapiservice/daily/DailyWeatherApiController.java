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
import org.springframework.web.bind.annotation.*;

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
public class DailyWeatherApiController {

    private final DailyWeatherService dailyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    public DailyWeatherApiController(DailyWeatherService dailyWeatherService, GeolocationService geolocationService, ModelMapper modelMapper) {
        this.dailyWeatherService = dailyWeatherService;
        this.geolocationService = geolocationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<DailyWeatherListDTO> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException, IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        Location locationFromIp = geolocationService.getLocation(ipAddress);

        List<DailyWeather> dailyWeatherList = dailyWeatherService.getByLocation(locationFromIp);

        if (dailyWeatherList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(listEntityToDTO(dailyWeatherList));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<DailyWeatherListDTO> listDailyForecastByLocationCode(
            @PathVariable("locationCode") String locationCode
    ){
        List<DailyWeather> dailyWeatherList = dailyWeatherService.getByLocationCode(locationCode);
        if (dailyWeatherList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(listEntityToDTO(dailyWeatherList));
    }

    @PostMapping("/{locationCode}")
    public ResponseEntity<DailyWeather> saveDailyForecast(
            @RequestBody DailyWeatherDTO dto,
            @PathVariable("locationCode") String locationCode){
        return ResponseEntity.ok().body(dailyWeatherService.save(dto,locationCode));
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
