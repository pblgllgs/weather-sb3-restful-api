package com.pblgllgs.weatherapiservice.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapicommon.common.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
class LocationApiControllerTests {

    private static final String END_POINT_PATH = "/v1/locations";
    private static final String END_POINT_PATH_NOT_ALLOWED = "/v1/locations/ASD";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    LocationService service;

    @Test
    void testAddShouldReturn400BadRequest() throws Exception {
        Location location = new Location();
        String bodyContent = mapper.writeValueAsString(location);
        mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void testAddShouldReturn201Created() throws Exception {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        Mockito.when(service.add(location)).thenReturn(location);
        String bodyContent = mapper.writeValueAsString(location);
        mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYC_USA"))
                .andDo(print());
    }

    @Test
    void testListShouldReturn204NoContent() throws Exception {
        Mockito.when(service.list()).thenReturn(Collections.emptyList());
        mockMvc
                .perform(
                        get(END_POINT_PATH)).
                andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testListShouldReturn200OK() throws Exception {
        Location location1 = new Location();
        location1.setCode("NYC_USA");
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryCode("US");
        location1.setCountryName("United States of America");
        location1.setEnabled(true);

        Location location2 = new Location();
        location2.setCode("CH_CL");
        location2.setCityName("CHILLAN");
        location2.setRegionName("ÑUBLE");
        location2.setCountryCode("CL");
        location2.setCountryName("CHILE");
        location2.setEnabled(true);

        Location location3 = new Location();
        location3.setCode("AL_AL");
        location3.setCityName("CITY");
        location3.setRegionName("CITY");
        location3.setCountryCode("AL");
        location3.setCountryName("ALBANIA");
        location3.setEnabled(true);


        Mockito.when(service.list()).thenReturn(Arrays.asList(location1, location2, location3));
        mockMvc
                .perform(
                        get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code", is("NYC_USA")))
                .andExpect(jsonPath("$[1].code", is("CH_CL")))
                .andExpect(jsonPath("$[2].code", is("AL_AL")))
                .andExpect(jsonPath("$[2].city_name", is("CITY")))
                .andDo(print());
    }

    @Test
    void testGetShouldReturn405NotAllowed() throws Exception {
        mockMvc
                .perform(
                        post(END_POINT_PATH_NOT_ALLOWED))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    void testGetLocation404NotFound() throws Exception {
        mockMvc
                .perform(
                        get(END_POINT_PATH_NOT_ALLOWED))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testGetLocation200Success() throws Exception {
        Location location1 = new Location();
        location1.setCode("NYC_USA");
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryCode("US");
        location1.setCountryName("United States of America");
        location1.setEnabled(true);
        String code = "CH_CL";
        String requestURI = END_POINT_PATH + "/" + code;
        Mockito.when(service.getLocation(code)).thenReturn(location1);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());
    }


    @Test
    void testUpdateShouldReturn404NotFound() throws Exception {
        Location location1 = new Location();
        location1.setCode("ASDQWE");
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryCode("US");
        location1.setCountryName("United States of America");
        location1.setEnabled(true);

        Mockito.when(service.update(location1)).thenThrow(new LocationNotFoundException("Location not found"));

        String bodyContent = mapper.writeValueAsString(location1);
        mockMvc
                .perform(
                        put(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequest() throws Exception {
        Location location1 = new Location();
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryCode("US");
        location1.setCountryName("United States of America");
        location1.setEnabled(false);

        String bodyContent = mapper.writeValueAsString(location1);
        mockMvc
                .perform(
                        put(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    void testUpdate200Success() throws Exception {
        Location location1 = new Location();
        location1.setCode("NYC_USA");
        location1.setCityName("new york city");
        location1.setRegionName("new york");
        location1.setCountryCode("us");
        location1.setCountryName("united states of america");
        location1.setEnabled(true);
        String code = "NYC_USA";

        Mockito.when(service.getLocation(code)).thenReturn(location1);

        mockMvc
                .perform(
                        get(END_POINT_PATH + "/" + code))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("new york city")))
                .andExpect(jsonPath("$.country_code", is("us")))
                .andExpect(jsonPath("$.country_name", is("united states of america")))
                .andDo(print());

        Location location2 = new Location();
        location2.setCode("NYC_USA");
        location2.setCityName("NEW YORK");
        location2.setRegionName("NEW YORK");
        location2.setCountryCode("US");
        location2.setCountryName("UNITED STATES OF AMERICA");
        location2.setEnabled(false);

        Mockito.when(service.update(location2)).thenReturn(location2);
        String bodyContent = mapper.writeValueAsString(location2);
        mockMvc
                .perform(
                        put(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("NEW YORK")))
                .andExpect(jsonPath("$.country_code", is("US")))
                .andExpect(jsonPath("$.country_name", is("UNITED STATES OF AMERICA")))
                .andDo(print());
    }

    @Test
    void testDelete404NotFound() throws Exception {
        String code = "CH_AR";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);
        mockMvc
                .perform(
                        delete(requestURI)
                                .contentType("application/json")
                ).andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testDelete204NoContent() throws Exception {
        String code = "CH_CL";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doNothing().when(service).delete(code);
        mockMvc
                .perform(
                        delete(requestURI)
                                .contentType("application/json")
                ).andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        Location location = new Location();
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);
        mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code cannot be null")))
                .andDo(print());
    }

    @Test
    void testValidateRequestBodyLocationCodeLength() throws Exception {
        Location location = new Location();
        location.setCode("CH");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(location);
        mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.errors[0]", is("Location code must ve 3-12 characters")))
                .andDo(print());
    }

    @Test
    void testValidateRequestBodyAllFieldsInvalid() throws Exception {
        Location location = new Location();
        location.setRegionName("");

        String bodyContent = mapper.writeValueAsString(location);
        MvcResult mvcResult = mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                ).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andDo(print())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertThat(responseBody)
                .contains("Location code cannot be null")
                .contains("City name cannot be null")
                .contains("Region name must ve 3-128 characters")
                .contains("Country name cannot be null")
                .contains("Country code cannot be null");
    }
}