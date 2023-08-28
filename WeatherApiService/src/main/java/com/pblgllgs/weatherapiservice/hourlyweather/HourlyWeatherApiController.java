package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapicommon.common.HourlyWeather;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@Slf4j
public class HourlyWeatherApiController {

    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper mapper;

    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService geolocationService, ModelMapper mapper) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.geolocationService = geolocationService;
        this.mapper = mapper;
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<Object> findHourlyWeatherForecastByLocationCode(
            HttpServletRequest request,
            @PathVariable("locationCode") String locationCode
    ) {
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            log.info(String.valueOf(currentHour));
            List<HourlyWeather> hourlyWeatherList = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            log.info(hourlyWeatherList.toString());
            if (hourlyWeatherList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntityToDTO(hourlyWeatherList));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Object> findHourlyWeatherForecastByIPAddress(HttpServletRequest request) throws IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location location = geolocationService.getLocation(ipAddress);
            List<HourlyWeather> hourlyWeatherList = hourlyWeatherService.getByLocation(location, currentHour);
            if (hourlyWeatherList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntityToDTO(hourlyWeatherList));
        } catch (NumberFormatException | GeolocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private HourlyWeatherListDTO listEntityToDTO(List<HourlyWeather> hourlyForecast) {
        Location location = hourlyForecast.get(0).getId().getLocation();
        HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
        listDTO.setLocation(location.toString());
        hourlyForecast.forEach(hourly -> {
            HourlyWeatherDTO hourlyWeatherDTO = mapper.map(hourly, HourlyWeatherDTO.class);
            listDTO.add(hourlyWeatherDTO);
        });
        return listDTO;
    }

}
