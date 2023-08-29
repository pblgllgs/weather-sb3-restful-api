package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/realtime")
public class RealtimeWeatherApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private final RealtimeWeatherService realtimeWeatherService;
    private final GeolocationService geolocationService;

    private final ModelMapper mapper;

    public RealtimeWeatherApiController(
            RealtimeWeatherService realtimeWeatherService,
            GeolocationService geolocationService,
            ModelMapper mapper) {
        this.realtimeWeatherService = realtimeWeatherService;
        this.geolocationService = geolocationService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<Object> getRealtimeWeatherByIpAddress(HttpServletRequest request) throws GeolocationException, IOException {
        String ipAddress = CommonUtility.getIPAddress(request);
        try {
            Location locationFromIP = geolocationService.getLocation(ipAddress);

            RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
            RealtimeWeatherDTO realtimeWeatherDTO = mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(realtimeWeatherDTO);
        } catch (GeolocationException e) {
            LOGGER.info(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<Object> getRealtimeWeatherByLocationCode(
            @PathVariable("locationCode") String code
    ) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(code);
        return ResponseEntity.ok(entity2DTO(realtimeWeather));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<Object> updateRealtimeWeatherByLocationCode(
            @PathVariable("locationCode") String code,
            @Valid @RequestBody RealtimeWeather realtimeWeatherRequest
    ) throws LocationNotFoundException {
        realtimeWeatherRequest.setLocationCode(code);
        RealtimeWeather realtimeWeatherUpdated = realtimeWeatherService.update(code, realtimeWeatherRequest);
        return ResponseEntity.ok(entity2DTO(realtimeWeatherUpdated));

    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }


}
