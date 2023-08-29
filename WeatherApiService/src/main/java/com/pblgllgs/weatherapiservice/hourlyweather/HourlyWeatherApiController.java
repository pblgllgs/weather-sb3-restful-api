package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapicommon.common.HourlyWeather;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {

    private static final String HEADER_HOUR = "X-Current-Hour";
    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService geolocationService;

    private final ModelMapper mapper;

    public HourlyWeatherApiController(
            HourlyWeatherService hourlyWeatherService,
            GeolocationService geolocationService,
            ModelMapper mapper) {
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
            int currentHour = Integer.parseInt(request.getHeader(HEADER_HOUR));
            List<HourlyWeather> hourlyWeatherList = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            if (hourlyWeatherList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntityToDTO(hourlyWeatherList));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Object> findHourlyWeatherForecastByIPAddress(
            HttpServletRequest request
    ) throws IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        try {
            int currentHour = Integer.parseInt(request.getHeader(HEADER_HOUR));
            Location location = geolocationService.getLocation(ipAddress);
            List<HourlyWeather> hourlyWeatherList = hourlyWeatherService.getByLocation(location, currentHour);
            if (hourlyWeatherList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntityToDTO(hourlyWeatherList));
        } catch (NumberFormatException | GeolocationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<Object> updateHourlyForecast(
            @PathVariable("locationCode") String locationCode,
            @Valid @RequestBody List<HourlyWeatherDTO> listHourly
    ) throws BadRequestException {
        if (listHourly.isEmpty()) {
            throw new BadRequestException("No hourly list");
        }
        List<HourlyWeather> listHourlyWeather = listHourlyWeatherDTOToEntity(listHourly);
        List<HourlyWeather> hourlyWeathersUpdated = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
        return ResponseEntity.ok(listEntityToDTO(hourlyWeathersUpdated));
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

    private List<HourlyWeather> listHourlyWeatherDTOToEntity(List<HourlyWeatherDTO> hourlyForecast) {
        List<HourlyWeather> list = new ArrayList<>();
        hourlyForecast.forEach(hourly -> {
            HourlyWeather hourlyWeather = mapper.map(hourly, HourlyWeather.class);
            list.add(hourlyWeather);
        });
        return list;
    }

}
