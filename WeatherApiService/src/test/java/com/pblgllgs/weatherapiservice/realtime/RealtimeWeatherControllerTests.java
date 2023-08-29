package com.pblgllgs.weatherapiservice.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealtimeWeatherApiController.class)
class RealtimeWeatherControllerTests {

    private static final String END_POINT_PATH = "/v1/realtime";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RealtimeWeatherService realtimeWeatherService;
    @MockBean
    GeolocationService geolocationService;

    @Test
    void testShouldReturnStatus400BadRequest() throws Exception {
        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testShouldReturnStatus404NotFound() throws Exception {
        Location location = new Location();
        location.setCountryCode("ASD");
        location.setCountryName("ASDASD");
        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        LocationNotFoundException locationNotFoundException = new LocationNotFoundException(location.getCountryCode(), location.getCityName());
        Mockito.when(realtimeWeatherService.getByLocation(location)).thenThrow(locationNotFoundException);
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testShouldReturnStatus200OK() throws Exception {
        Location location = new Location();
        location.setCode("SFCA_USA");
        location.setCityName("San Francisco");
        location.setRegionName("California");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(realtimeWeatherService.getByLocation(location)).thenReturn(realtimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }

    @Test
    void testUpdateRealtimeShouldReturn404NotFound() throws Exception {
        String locationCode = "ASD";
        String url = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);
        realtimeWeather.setLocationCode(locationCode);

        LocationNotFoundException locationNotFoundException = new LocationNotFoundException(locationCode);
        Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(locationNotFoundException);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(url).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testUpdateRealtimeShouldReturn400BadRequest() throws Exception {
        String locationCode = "ASD";
        String url = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(120);
        realtimeWeather.setHumidity(132);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(188);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(530);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(url).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testUpdateRealtimeShouldReturn200OK() throws Exception {
        Location location = new Location();
        location.setCode("CL");
        location.setCityName("Santiago");
        location.setRegionName("Region Metropolitana de Santiago");
        location.setCountryName("CHILE");
        location.setCountryCode("CL");

        String locationCode = "CL";
        String url = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(15);
        realtimeWeather.setHumidity(20);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(25);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(30);
        realtimeWeather.setLocation(location);

        realtimeWeather.setLocation(location);
        location.setRealtimeWeather(realtimeWeather);

        Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);

        String bodyContent = mapper.writeValueAsString(realtimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(put(url).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }
}
