package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherApiController;
import com.pblgllgs.weatherapiservice.full.FullWeatherApiController;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherApiController;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
            return ResponseEntity.ok(addLinksByIp(realtimeWeatherDTO));
        } catch (GeolocationException e) {
            LOGGER.info(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<Object> getRealtimeWeatherByLocationCode(
            @PathVariable("locationCode") String locationCode
    ) throws LocationNotFoundException, GeolocationException, IOException {
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
        RealtimeWeatherDTO realtimeWeatherDTO = entity2DTO(realtimeWeather);
        return ResponseEntity.ok(addLinksByLocationCode(realtimeWeatherDTO,locationCode));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<Object> updateRealtimeWeatherByLocationCode(
            @PathVariable("locationCode") String locationCode,
            @Valid @RequestBody RealtimeWeatherDTO realtimeWeatherRequestDto
    ) throws LocationNotFoundException, GeolocationException, IOException {
        RealtimeWeather realtimeWeatherRequest = dtoToEntity(realtimeWeatherRequestDto);
        realtimeWeatherRequest.setLocationCode(locationCode);
        RealtimeWeather realtimeWeatherUpdated = realtimeWeatherService.update(locationCode, realtimeWeatherRequest);
        RealtimeWeatherDTO realtimeWeatherDTO = entity2DTO(realtimeWeatherUpdated);
        return ResponseEntity.ok(addLinksByLocationCode(realtimeWeatherDTO,locationCode));

    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }

    private RealtimeWeather dtoToEntity(RealtimeWeatherDTO realtimeWeather) {
        return mapper.map(realtimeWeather, RealtimeWeather.class);
    }

    private RealtimeWeatherDTO addLinksByIp(RealtimeWeatherDTO dto) throws GeolocationException, IOException {
        dto.add(
                linkTo(
                        methodOn(RealtimeWeatherApiController.class)
                                .getRealtimeWeatherByIpAddress(null)
                ).withSelfRel());
        dto.add(
                linkTo(
                        methodOn(HourlyWeatherApiController.class)
                                .findHourlyWeatherForecastByIPAddress(null)
                ).withRel("hourly_forecast"));
        dto.add(
                linkTo(
                        methodOn(DailyWeatherApiController.class)
                                .listDailyForecastByIPAddress(null)
                ).withRel("daily_forecast"));
        dto.add(
                linkTo(
                        methodOn(FullWeatherApiController.class)
                                .getFullWeatherByIPAddress(null)
                ).withRel("full_forecast"));
        return dto;
    }

    private RealtimeWeatherDTO addLinksByLocationCode(
            RealtimeWeatherDTO dto,
            String locationCode
    ) throws GeolocationException, IOException {
        dto.add(
                linkTo(
                        methodOn(RealtimeWeatherApiController.class)
                                .getRealtimeWeatherByLocationCode(locationCode)
                ).withSelfRel());
        dto.add(
                linkTo(
                        methodOn(HourlyWeatherApiController.class)
                                .findHourlyWeatherForecastByLocationCode(null, locationCode)
                ).withRel("hourly_forecast"));
        dto.add(
                linkTo(
                        methodOn(DailyWeatherApiController.class)
                                .listDailyForecastByLocationCode(locationCode)
                ).withRel("daily_forecast"));
        dto.add(
                linkTo(
                        methodOn(FullWeatherApiController.class)
                                .getFullWeatherByLocationCode(locationCode)
                ).withRel("full_forecast"));
        return dto;
    }


}
