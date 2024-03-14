package com.pblgllgs.weatherapiservice.full;

import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class FullWeatherApiController {

    private final GeolocationService geolocationService;
    private final FullWeatherService fullWeatherService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<FullWeatherDTO> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException, IOException {

        String ipAddress = CommonUtility.getIPAddress(request);
        Location locationFromIP = geolocationService.getLocation(ipAddress);
        Location locationByIPAddressInDB = fullWeatherService.getByLocation(locationFromIP);
        return ResponseEntity.ok().body(entityToDTO(locationByIPAddressInDB));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<FullWeatherDTO> getFullWeatherByLocationCode(
            @PathVariable("locationCode") String locationCode) {
        Location locationByLocationCodeInDB = fullWeatherService.getByLocationCode(locationCode);
        return ResponseEntity.ok().body(entityToDTO(locationByLocationCodeInDB));
    }

    private FullWeatherDTO entityToDTO(Location location) {
        FullWeatherDTO dto = modelMapper.map(location, FullWeatherDTO.class);
        dto.getRealtimeWeather().setLocation(null);
        return dto;
    }
}
