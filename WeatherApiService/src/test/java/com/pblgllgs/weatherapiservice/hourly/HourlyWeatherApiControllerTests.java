package com.pblgllgs.weatherapiservice.hourly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherApiController;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HourlyWeatherApiController.class)
@Slf4j
class HourlyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/hourly";
    private static final String X_CURRENT_HOUR = "X-Current-Hour";

    public static final String APPLICATION_HAL_JSON = "application/hal+json";


    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    HourlyWeatherService hourlyWeatherService;
    @MockBean
    GeolocationService geolocationService;


    @Test
    void testGetByIpShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        mockMvc
                .perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
        mockMvc
                .perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        Location location = new Location().code("NYC_US");
        when(geolocationService.getLocation(location.getCode())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());
        mockMvc
                .perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("MBMH_IN");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(15)
                .temperature(20)
                .precipitation(60)
                .status("Sunny");


        int currentHour = 9;

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc
                .perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/hourly")))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime")))
                .andExpect(jsonPath("$._links.daily_forecast.href",is("http://localhost/v1/daily")))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full")))
                .andDo(print());
    }

    @Test
    void testGetByLocationCodeShouldReturn200OK() throws Exception {
        Location location = new Location();
        String locationCode = "CL";
        location.setCode(locationCode);
        location.setCityName("Santiago");
        location.setRegionName("Metropolitana");
        location.setCountryCode("CL");
        location.setCountryName("CHILE");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(15)
                .temperature(20)
                .precipitation(60)
                .status("Sunny");


        int currentHour = 7;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc
                .perform(get(END_POINT_PATH + "/" + locationCode)
                        .header(
                                X_CURRENT_HOUR,
                                String.valueOf(currentHour))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/hourly/"+locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime/"+locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href",is("http://localhost/v1/daily/"+locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full/"+locationCode)))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String locationCode = "CL";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        List<HourlyWeather> listEmpty =Collections.emptyList();
        String requestBody =  mapper.writeValueAsString(listEmpty);

        mockMvc
                .perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.Error",is("Bad Request")))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String locationCode = "CL";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO forecast1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(500)
                .precipitation(150)
                .status("");

        HourlyWeatherDTO forecast2 = new HourlyWeatherDTO()
                .hourOfDay(15)
                .temperature(480)
                .precipitation(-100)
                .status("");

        List<HourlyWeatherDTO> listDTO = List.of(forecast1,forecast2);

        String requestBody =  mapper.writeValueAsString(listDTO);

        mockMvc
                .perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "CL";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO forecast1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(50)
                .precipitation(20)
                .status("Sunny");


        List<HourlyWeatherDTO> listDTO = List.of(forecast1);

        String requestBody =  mapper.writeValueAsString(listDTO);

        Mockito.when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode),Mockito.anyList())).thenThrow(LocationNotFoundException.class);

        mockMvc
                .perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "CL";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(30)
                .precipitation(20)
                .status("Cloudy");

        HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
                .hourOfDay(15)
                .temperature(40)
                .precipitation(25)
                .status("Sunny");

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("Santiago");
        location.setRegionName("Region Metropolitana de Santiago");
        location.setCountryCode("CL");
        location.setCountryName("CHILE");

        HourlyWeather forecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Sunny");

        HourlyWeather forecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(15)
                .temperature(20)
                .precipitation(60)
                .status("Cloudy");

        List<HourlyWeatherDTO> listHourlyWeatherDTO = List.of(dto1,dto2);

        List<HourlyWeather> listHourlyForecast = List.of(forecast1, forecast2);

        String requestBody =  mapper.writeValueAsString(listHourlyWeatherDTO);

        Mockito.when(
                hourlyWeatherService
                        .updateByLocationCode(
                                Mockito.eq(locationCode),
                                Mockito.anyList()))
                .thenReturn(listHourlyForecast);

        mockMvc
                .perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.location",is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day",is(10)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/hourly/"+locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime/"+locationCode)))
                .andExpect(jsonPath("$._links.daily_forecast.href",is("http://localhost/v1/daily/"+locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full/"+locationCode)))
                .andDo(print());
    }
}
