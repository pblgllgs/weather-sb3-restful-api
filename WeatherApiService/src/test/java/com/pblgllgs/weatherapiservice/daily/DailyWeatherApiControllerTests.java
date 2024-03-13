package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */
@WebMvcTest(DailyWeatherApiController.class)
@Slf4j
public class DailyWeatherApiControllerTests {

    private static final String END_POINT_PATH = "/v1/daily";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyWeatherService dailyWeatherServiceMock;
    @MockBean
    private LocationService locationServiceMock;

    @Test
    public void testGetByIpShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        GeolocationException ex = new GeolocationException("Geolocation error");
        when(locationServiceMock.getLocation(anyString())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIpShouldReturn404NotFound() throws Exception {
        Location location = new Location().code("NYC_US");

        when(locationServiceMock.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(dailyWeatherServiceMock.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIpShouldReturn204NoContent() throws Exception {
        Location location = new Location().code("NYC_US");
        when(locationServiceMock.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherServiceMock.getByLocation(location)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
