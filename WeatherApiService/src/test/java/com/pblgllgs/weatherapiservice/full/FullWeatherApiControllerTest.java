package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherApiController;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherApiController.class)
@Slf4j
class FullWeatherApiControllerTest {

    private static final String END_POINT_PATH = "/v1/full";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FullWeatherService fullWeatherService;
    @MockBean
    private GeolocationService locationServiceMock;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testGetByIpShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        GeolocationException ex = new GeolocationException("Geolocation error");
        when(locationServiceMock.getLocation(anyString())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
                .andDo(print());
    }

    @Test
    void testGetByIpShouldReturn404NotFound() throws Exception {
        Location location = new Location().code("ASD");

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(fullWeatherService.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
                .andDo(print());
    }

//    @Test
//    void testGetByIpShouldReturn204NoContent() throws Exception {
//        Location location = new Location().code("NYC_US");
//        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
//        when(fullWeatherService.getByLocation(location)).thenReturn(Object.class);
//        mockMvc.perform(get(END_POINT_PATH))
//                .andExpect(status().isNoContent())
//                .andDo(print());
//    }

    @Test
    void testGetByIpShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("NYC_US");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(15);
        realtimeWeather.setHumidity(20);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(25);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(30);

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        DailyWeather dailyForecast1 = new DailyWeather().location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(25)
                .precipitation(40)
                .status("Cloudy");

        DailyWeather dailyForecast2 = new DailyWeather().location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny");

        location.setListDailyWeather(List.of(dailyForecast1, dailyForecast2));

        HourlyWeather hourlyForecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        HourlyWeather hourlyForecast2 = new HourlyWeather()
                .location(location)
                .hourOfDay(15)
                .temperature(20)
                .precipitation(60)
                .status("Sunny");

        location.setListHourlyWeather(List.of(hourlyForecast1,hourlyForecast2));

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
        when(fullWeatherService.getByLocation(location)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(15)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[1].day_of_month", is(17)))
                .andDo(print());
    }
}
