package com.pblgllgs.weatherapiservice.full;

import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.Location;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */
@RestController
@RequestMapping("/v1/full")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FullWeatherApiController {

    private final GeolocationService geolocationService;
    private final FullWeatherService fullWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<FullWeatherDTO> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException, IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        Location locationFromIP = geolocationService.getLocation(ipAddress);
        Location locationByIPAddressInDB = fullWeatherService.getByLocation(locationFromIP);
        return ResponseEntity.ok(entityToDTO(locationByIPAddressInDB));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<FullWeatherDTO> getFullWeatherByLocationCode(
            @PathVariable("locationCode") String locationCode) {
        Location locationByLocationCodeInDB = fullWeatherService.getByLocationCode(locationCode);
        return ResponseEntity.ok().body(entityToDTO(locationByLocationCodeInDB));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> update(
            @PathVariable("locationCode") String locationCode,
            @RequestBody @Valid FullWeatherDTO dto
    ) throws BadRequestException {
        if (dto.getListHourlyWeather().isEmpty()) {
            throw new BadRequestException("Hourly weather data can't be empty");
        }
        if (dto.getListDailyWeather().isEmpty()) {
            throw new BadRequestException("Daily weather data can't be empty");
        }
        if (dto.getRealtimeWeather() == null) {
            throw new BadRequestException("Realtime weather data can't be empty");
        }
        Location location = dtoToEntity(dto);
        Location updatedLocation = fullWeatherService.update(locationCode, location);
        return ResponseEntity.ok(entityToDTO(updatedLocation));
    }


    private FullWeatherDTO entityToDTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);
        dto.getRealtimeWeather().setLocation(null);
        return dto;
    }

    private Location dtoToEntity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }
}
