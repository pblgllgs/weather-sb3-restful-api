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
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherApiController.class)
@Slf4j
class FullWeatherApiControllerTest {

    private static final String END_POINT_PATH = "/v1/full";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FullWeatherService fullWeatherServiceMock;
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
        when(fullWeatherServiceMock.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
                .andDo(print());
    }

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

        location.setListHourlyWeather(List.of(hourlyForecast1, hourlyForecast2));

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
        when(fullWeatherServiceMock.getByLocation(location)).thenReturn(location);

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

    @Test
    void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "ASDFG";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherServiceMock.getByLocationCode(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
                .andDo(print());
    }

    @Test
    void testGetByCodeShouldReturn200OK() throws Exception {

        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
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

        location.setListHourlyWeather(List.of(hourlyForecast1, hourlyForecast2));

        when(fullWeatherServiceMock.getByLocationCode(locationCode)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(15)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[1].day_of_month", is(17)))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseNoHourlyWeather() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO dto = new FullWeatherDTO();

        String requestBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "Hourly weather data can't be empty"))))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseNoDailyWeather() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        HourlyWeatherDTO hourlyForecast2 = new HourlyWeatherDTO()
                .hourOfDay(15)
                .temperature(20)
                .precipitation(60)
                .status("Sunny");


        fullWeatherDTO.getListHourlyWeather().addAll(List.of(hourlyForecast1, hourlyForecast2));

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "Daily weather data can't be empty"))))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseNoRealtimeWeather() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
                .hourOfDay(10)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dto1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(150);
        realtimeDTO.setHumidity(150);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(25);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(30);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(
                        Map.of(
                                "realtimeWeather.temperature", "Temperature must be in the range -50 to 50 C°"
                                , "realtimeWeather.humidity", "Humidity must be in the range 0 to 100 percentage"
                        )
                )))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseInvalidHourlyWeather() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
                .hourOfDay(55)
                .temperature(250)
                .precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dto1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(25);
        realtimeDTO.setHumidity(30);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(25);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(30);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(
                        Map.of(
                                "listHourlyWeather[0].temperature", "Temperature must be in the range -50 to 50 C°",
                                "listHourlyWeather[0].hourOfDay", "Hour of day must be in the range -1 to 24"
                        )
                )))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequestBecauseInvalidDailyWeather() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
                .hourOfDay(22)
                .temperature(30)
                .precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(50)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dto1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(25);
        realtimeDTO.setHumidity(30);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(25);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(30);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(
                        Map.of(
                                "listDailyWeather[0].dayOfMonth", "Day of the month must be between 1-31"
                        )
                )))
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn404NotFound() throws Exception {

        String locationCode = "USA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
                .hourOfDay(22)
                .temperature(30)
                .precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = DailyWeatherDTO.builder()
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecast1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(25);
        realtimeDTO.setHumidity(30);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(25);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(30);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherServiceMock.update(Mockito.eq(locationCode),Mockito.any())).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", ex.getMessage()))))
                .andDo(print());

    }

    @Test
    void testUpdateShouldReturn200() throws Exception {
        String locationCode = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryName("United States of America");
        location.setCountryCode("US");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(25);
        realtimeWeather.setHumidity(30);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(25);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(30);

        location.setRealtimeWeather(realtimeWeather);

        DailyWeather dailyWeather = new DailyWeather()
                .location(location)
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear");

        location.setListDailyWeather(List.of(dailyWeather));

        HourlyWeather hourlyForecast1 = new HourlyWeather()
                .location(location)
                .hourOfDay(22)
                .temperature(30)
                .precipitation(50)
                .status("Cloudy");

        location.setListHourlyWeather(List.of(hourlyForecast1));

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecastDTO1 = new HourlyWeatherDTO()
                .hourOfDay(22)
                .temperature(30)
                .precipitation(50)
                .status("Cloudy");

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecastDTO1);

        DailyWeatherDTO dailyForecastDTO1 = DailyWeatherDTO.builder()
                .dayOfMonth(25)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecastDTO1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(25);
        realtimeDTO.setHumidity(30);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(25);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(30);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        when(fullWeatherServiceMock.update(Mockito.eq(locationCode), Mockito.any())).thenReturn(location);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realtime_weather.temperature", is(25)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(22)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(25)))
                .andDo(print());
    }
}
