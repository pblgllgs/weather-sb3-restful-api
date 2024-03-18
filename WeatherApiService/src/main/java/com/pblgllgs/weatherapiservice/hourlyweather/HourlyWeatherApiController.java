package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.CommonUtility;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherApiController;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.full.FullWeatherApiController;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherApiController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @GetMapping
    public ResponseEntity<HourlyWeatherListDTO> findHourlyWeatherForecastByIPAddress(
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
            HourlyWeatherListDTO hourlyWeatherListDTO = listEntityToDTO(hourlyWeatherList);
            return ResponseEntity.ok(addLinksByIp(hourlyWeatherListDTO));
        } catch (NumberFormatException | GeolocationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<HourlyWeatherListDTO> findHourlyWeatherForecastByLocationCode(
            HttpServletRequest request,
            @PathVariable("locationCode") String locationCode
    ) {
        try {
            int currentHour = Integer.parseInt(request.getHeader(HEADER_HOUR));
            List<HourlyWeather> hourlyWeatherList = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            if (hourlyWeatherList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            HourlyWeatherListDTO hourlyWeatherListDTO = listEntityToDTO(hourlyWeatherList);
            return ResponseEntity.ok(addLinksByLocationCode(hourlyWeatherListDTO,locationCode));
        } catch (NumberFormatException | GeolocationException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<HourlyWeatherListDTO> updateHourlyForecast(
            @PathVariable("locationCode") String locationCode,
            @Valid @RequestBody List<HourlyWeatherDTO> listDTO
    ) throws BadRequestException, GeolocationException, IOException {
        if (listDTO.isEmpty()) {
            throw new BadRequestException("No hourly list");
        }
        List<HourlyWeather> listHourlyWeather = listHourlyWeatherDTOToEntity(listDTO);
        List<HourlyWeather> hourlyWeathersUpdated = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
        HourlyWeatherListDTO hourlyWeatherListDTO= listEntityToDTO(hourlyWeathersUpdated);
        return ResponseEntity.ok(addLinksByLocationCode(hourlyWeatherListDTO,locationCode));
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

    private HourlyWeatherListDTO addLinksByIp(HourlyWeatherListDTO dto) throws GeolocationException, IOException {
        dto.add(
                linkTo(
                        methodOn(HourlyWeatherApiController.class)
                                .findHourlyWeatherForecastByIPAddress(null)
                ).withSelfRel());
        dto.add(
                linkTo(
                        methodOn(DailyWeatherApiController.class)
                                .listDailyForecastByIPAddress(null)
                ).withRel("daily_forecast"));
        dto.add(
                linkTo(
                        methodOn(RealtimeWeatherApiController.class)
                                .getRealtimeWeatherByIpAddress(null)
                ).withRel("realtime_weather"));
        dto.add(
                linkTo(
                        methodOn(FullWeatherApiController.class)
                                .getFullWeatherByIPAddress(null)
                ).withRel("full_forecast"));
        return dto;
    }

    private HourlyWeatherListDTO addLinksByLocationCode(
            HourlyWeatherListDTO dto,
            String locationCode
    ) throws GeolocationException, IOException {
        dto.add(
                linkTo(
                        methodOn(HourlyWeatherApiController.class)
                                .findHourlyWeatherForecastByLocationCode(null, locationCode)
                ).withSelfRel());
        dto.add(
                linkTo(
                        methodOn(DailyWeatherApiController.class)
                                .listDailyForecastByLocationCode(locationCode)
                ).withRel("daily_forecast"));
        dto.add(
                linkTo(
                        methodOn(RealtimeWeatherApiController.class)
                                .getRealtimeWeatherByLocationCode(locationCode)
                ).withRel("realtime_weather"));
        dto.add(
                linkTo(
                        methodOn(FullWeatherApiController.class)
                                .getFullWeatherByLocationCode(locationCode)
                ).withRel("full_forecast"));
        return dto;
    }

}
