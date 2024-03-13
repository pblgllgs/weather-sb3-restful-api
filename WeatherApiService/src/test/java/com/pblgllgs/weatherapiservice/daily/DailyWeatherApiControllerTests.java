package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */
@WebMvcTest(DailyWeatherApiController.class)
@Slf4j
class DailyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/daily";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyWeatherService dailyWeatherServiceMock;
    @MockBean
    private GeolocationService locationServiceMock;

    @Test
    void testGetByIpShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        GeolocationException ex = new GeolocationException("Geolocation error");
        when(locationServiceMock.getLocation(anyString())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error",ex.getMessage()))))
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn404NotFound() throws Exception {
        Location location = new Location().code("NYC_US");

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(dailyWeatherServiceMock.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error",ex.getMessage()))))
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn204NoContent() throws Exception {
        Location location = new Location().code("NYC_US");
        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherServiceMock.getByLocation(location)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setCountryName("Ney York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        DailyWeather forecast1 = new DailyWeather().location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(25)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather forecast2 = new DailyWeather().location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherServiceMock.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andDo(print());
    }

    @Test
    void testGetByLocationCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "LACA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;
        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherServiceMock.getByLocationCode(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error",ex.getMessage()))))
                .andDo(print());
    }

    @Test
    void testGetByLocationCodeShouldReturn204NoContent() throws Exception {
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;
        when(dailyWeatherServiceMock.getByLocationCode(locationCode)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    void testGetByLocationCodeShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setCountryName("Ney York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        DailyWeather forecast1 = new DailyWeather().location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(25)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather forecast2 = new DailyWeather().location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        when(dailyWeatherServiceMock.getByLocationCode(locationCode)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andDo(print());
    }
}
