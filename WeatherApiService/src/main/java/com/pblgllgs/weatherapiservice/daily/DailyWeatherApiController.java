package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.DailyWeatherId;
import com.pblgllgs.weatherapiservice.common.Location;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */
@RestController
@RequestMapping("/v1/daily")
@Validated
@Slf4j
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

        if (dailyWeatherList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(listEntityToDTO(dailyWeatherList));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<DailyWeatherListDTO> listDailyForecastByLocationCode(
            @PathVariable("locationCode") String locationCode
    ) {
        List<DailyWeather> dailyWeatherList = dailyWeatherService.getByLocationCode(locationCode);
        if (dailyWeatherList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(listEntityToDTO(dailyWeatherList));
    }

    @PostMapping("/{locationCode}")
    public ResponseEntity<DailyWeather> saveDailyForecast(
            @RequestBody DailyWeatherDTO dto,
            @PathVariable("locationCode") String locationCode) {
        return ResponseEntity.ok().body(dailyWeatherService.save(dto, locationCode));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateDailyForecast(
            @PathVariable("locationCode") String code,
            @RequestBody @Valid List<DailyWeatherDTO> listDto
    ) throws BadRequestException {
        if (listDto.isEmpty()) {
            throw new BadRequestException("Daily forecast data can't be empty");
        }

        log.info("============DTO=============");
        listDto.forEach(dto -> log.info(String.valueOf(dto.toString())));

        List<DailyWeather> dailyWeathers = listDTOToListEntity(listDto);

        log.info("===========ENTITY==============");
        dailyWeathers.forEach(dto -> log.info(String.valueOf(dto.toString())));

        List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeathers);

        return ResponseEntity.ok(listEntityToDTO(updatedForecast));
    }

    private List<DailyWeather> listDTOToListEntity(List<DailyWeatherDTO> listDto) {
        List<DailyWeather> list = new ArrayList<>();
        listDto.forEach(dto -> {
            DailyWeather dailyWeather = DailyWeather.builder()
                    .id(genId(dto.getDayOfMonth(), dto.getMonth()))
                    .maxTemp(dto.getMaxTemp())
                    .minTemp(dto.getMinTemp())
                    .precipitation(dto.getPrecipitation())
                    .status(dto.getStatus())
                    .build();
            list.add(dailyWeather);
        });
        return list;
    }

    private DailyWeatherId genId(int dayOfMonth, int month) {
        return DailyWeatherId.builder()
                .dayOfMonth(dayOfMonth)
                .month(month)
                .build();
    }

    private DailyWeatherListDTO listEntityToDTO(List<DailyWeather> dailyWeatherList) {
        Location location = dailyWeatherList.get(0).getId().getLocation();
        DailyWeatherListDTO dto = new DailyWeatherListDTO();
        dto.setLocation(location.toString());
        dailyWeatherList.forEach(dailyWeather -> {
            dto.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
        });

        return dto;
    }
}
