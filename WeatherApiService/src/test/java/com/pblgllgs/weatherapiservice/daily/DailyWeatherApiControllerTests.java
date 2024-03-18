package com.pblgllgs.weatherapiservice.daily;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.GeolocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
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
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    public static final String APPLICATION_HAL_JSON = "application/hal+json";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyWeatherService dailyWeatherServiceMock;
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
        Location location = new Location().code("NYC_US");

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(dailyWeatherServiceMock.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
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
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/daily")))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime")))
                .andExpect(jsonPath("$._links.hourly_forecast.href",is("http://localhost/v1/hourly")))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full")))
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
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
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
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/daily/"+locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime/"+locationCode)))
                .andExpect(jsonPath("$._links.hourly_forecast.href",is("http://localhost/v1/hourly/"+locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full/"+locationCode)))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";
        List<DailyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "Daily forecast data can't be empty"))))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_US";
        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(40)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        DailyWeatherDTO dto2 = DailyWeatherDTO.builder()
                .dayOfMonth(35)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        List<DailyWeatherDTO> dtos = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(dtos);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.Error",
                        containsString("updateDailyForecast.listDto[0].dayOfMonth: Day of the month must be between 1-31, updateDailyForecast.listDto[1].dayOfMonth: Day of the month must be between 1-31")))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + locationCode;
        DailyWeatherDTO dto = DailyWeatherDTO.builder()
                .dayOfMonth(40)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        List<DailyWeatherDTO> dtos = List.of(dto);

        String requestBody = objectMapper.writeValueAsString(dtos);
        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherServiceMock.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList())).thenThrow(ex);
        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH +"/"+ locationCode;

        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        DailyWeatherDTO dto2 = DailyWeatherDTO.builder()
                .dayOfMonth(20)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        DailyWeather forecast1 = new DailyWeather()
                .location(location)
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(35)
                .precipitation(50)
                .status("Clear");

        DailyWeather forecast2 = new DailyWeather()
                .location(location)
                .dayOfMonth(18)
                .month(7)
                .minTemp(26)
                .maxTemp(34)
                .precipitation(40)
                .status("Sunny");

        List<DailyWeather> dailyWeatherList = List.of(forecast1, forecast2);

        List<DailyWeatherDTO> dailyWeatherDTOSList = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(dailyWeatherDTOSList);
        when(
                dailyWeatherServiceMock.
                        updateByLocationCode
                                (Mockito.eq(locationCode),
                                        Mockito.anyList()
                                )
        ).thenReturn(dailyWeatherList);
        mockMvc.perform(put(requestURI).contentType(APPLICATION_HAL_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(17)))
                .andExpect(jsonPath("$._links.self.href",is("http://localhost/v1/daily/"+locationCode)))
                .andExpect(jsonPath("$._links.realtime_weather.href",is("http://localhost/v1/realtime/"+locationCode)))
                .andExpect(jsonPath("$._links.hourly_forecast.href",is("http://localhost/v1/hourly/"+locationCode)))
                .andExpect(jsonPath("$._links.full_forecast.href",is("http://localhost/v1/full/"+locationCode)))
                .andDo(print());
    }
}
