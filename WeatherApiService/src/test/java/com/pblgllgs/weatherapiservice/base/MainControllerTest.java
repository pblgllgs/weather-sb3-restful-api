package com.pblgllgs.weatherapiservice.base;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*
 *
 * @author pblgl
 * Created on 18-03-2024
 *
 */
@WebMvcTest(MainController.class)
class MainControllerTest {

    private static final String BASE_URI= "/";
    public static final String APPLICATION_JSON = "application/json";

    @Autowired private MockMvc mockMvc;

    @Test
    void testBaseURI() throws Exception {

        mockMvc.perform(get(BASE_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.locations_url",is("http://localhost/v1/locations")))
                .andExpect(jsonPath("$.location_by_code_url",is("http://localhost/v1/locations/{code}")))
                .andExpect(jsonPath("$.realtime_weather_by_ip_url",is("http://localhost/v1/realtime")))
                .andExpect(jsonPath("$.realtime_weather_by_location_code_url",is("http://localhost/v1/realtime/{locationCode}")))
                .andExpect(jsonPath("$.daily_forecast_by_ip_url",is("http://localhost/v1/daily")))
                .andExpect(jsonPath("$.daily_forecast_by_location_code_url",is("http://localhost/v1/daily/{locationCode}")))
                .andExpect(jsonPath("$.hourly_forecast_by_ip_url",is("http://localhost/v1/hourly")))
                .andExpect(jsonPath("$.hourly_forecast_by_location_code_url",is("http://localhost/v1/hourly/{locationCode}")))
                .andExpect(jsonPath("$.full_weather_by_ip_url",is("http://localhost/v1/full")))
                .andExpect(jsonPath("$.full_weather_by_location_code_url",is("http://localhost/v1/full/{locationCode}")))
                .andDo(print());
    }
}
