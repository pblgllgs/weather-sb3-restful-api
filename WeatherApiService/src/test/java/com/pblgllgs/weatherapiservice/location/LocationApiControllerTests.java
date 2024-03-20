package com.pblgllgs.weatherapiservice.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pblgllgs.weatherapiservice.common.Location;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationApiController.class)
class LocationApiControllerTests {

    private static final String END_POINT_PATH = "/v1/locations";
    private static final String APPLICATION_HAL_JSON = "application/hal+json";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    LocationService locationService;

    @Test
    void testAddShouldReturn400BadRequest() throws Exception {
        LocationDTO location = new LocationDTO();
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
        location.setCode("NYCS_US");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCode(location.getCode());
        locationDTO.setCityName(location.getCityName());
        locationDTO.setRegionName(location.getRegionName());
        locationDTO.setCountryCode(location.getCountryCode());
        locationDTO.setCountryName(location.getCountryName());
        locationDTO.setEnabled(location.isEnabled());


        Mockito.when(locationService.add(location)).thenReturn(location);
        String bodyContent = mapper.writeValueAsString(locationDTO);
        mockMvc
                .perform(
                        post(END_POINT_PATH)
                                .contentType("application/json")
                                .content(bodyContent)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.code", is("NYCS_US")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andExpect(header().string("Location", "/v1/locations/NYCS_US"))
                .andDo(print());
    }

    @Test
    @Disabled
    void testListShouldReturn204NoContent() throws Exception {
        Mockito.when(locationService.list()).thenReturn(Collections.emptyList());
        mockMvc
                .perform(
                        get(END_POINT_PATH)).
                andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Disabled
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


        Mockito.when(locationService.list()).thenReturn(Arrays.asList(location1, location2, location3));
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
    void testListPageShouldReturn204NoContent() throws Exception {
        Mockito.when(
                        locationService.listByPage(
                                Mockito.anyInt(),
                                Mockito.anyInt(),
                                Mockito.anyString(),
                                Mockito.anyMap()
                        ))
                .thenReturn(Page.empty());
        mockMvc
                .perform(
                        get(END_POINT_PATH)).
                andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testListLocationPageShouldReturn400BadRequestInvalidPageNum() throws Exception {
        int pageNum = -1;
        int pageSize = 5;
        String sortField = "code";
        Mockito.when(
                        locationService.listByPage(
                                Mockito.anyInt(),
                                Mockito.anyInt(),
                                Mockito.anyString(),
                                Mockito.anyMap()
                        ))
                .thenReturn(Page.empty());
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
        mockMvc
                .perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "listLocation.pageNum: must be greater than or equal to 1"))))
                .andDo(print());
    }

    @Test
    void testListLocationPageShouldReturn400BadRequestInvalidPageSize() throws Exception {
        int pageNum = 1;
        int pageSize = 1;
        String sortField = "code";
        Mockito.when(
                        locationService.listByPage(
                                Mockito.anyInt(),
                                Mockito.anyInt(),
                                Mockito.anyString(),
                                Mockito.anyMap()
                        ))
                .thenReturn(Page.empty());
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
        mockMvc
                .perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "listLocation.pageSize: must be greater than or equal to 3"))))
                .andDo(print());
    }

    @Test
    void testListLocationPageShouldReturn400BadRequestInvalidSortField() throws Exception {
        int pageNum = 1;
        int pageSize = 3;
        String sortField = "code_fail";
        Mockito.when(
                        locationService.listByPage(
                                Mockito.anyInt(),
                                Mockito.anyInt(),
                                Mockito.anyString(),
                                Mockito.anyMap()
                        ))
                .thenReturn(Page.empty());
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;
        mockMvc
                .perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", is(Map.of("Error", "Invalid Sort Field: " + sortField))))
                .andDo(print());
    }

    @Test
    void testListPageShouldReturn200OK() throws Exception {
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

        List<Location> locations = List.of(location1, location2, location3);

        int pageNum = 1;
        int pageSize = 3;
        String sortField = "code";
        int totalElements = locations.size();

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Location> page = new PageImpl<>(locations, pageable, totalElements);

        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        Mockito.when(
                locationService.listByPage(
                        Mockito.anyInt(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyMap()
                )).thenReturn(page);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.locations[0].code", is("NYC_USA")))
                .andExpect(jsonPath("$._embedded.locations[1].city_name", is("CHILLAN")))
                .andExpect(jsonPath("$._embedded.locations[2].code", is("AL_AL")))
                .andExpect(jsonPath("$._embedded.locations[2].city_name", is("CITY")))
                .andExpect(jsonPath("$.page.size", is(pageSize)))
                .andExpect(jsonPath("$.page.number", is(pageNum)))
                .andExpect(jsonPath("$.page.total_elements", is(totalElements)))
                .andExpect(jsonPath("$.page.total_pages", is(1)))
                .andDo(print());
    }

    @Test
    void testListPaginationLinksOnlyOnePage() throws Exception {
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


        List<Location> locations = List.of(location1, location2);

        int pageNum = 1;
        int pageSize = 3;
        String sortField = "code";
        int totalElements = locations.size();

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Location> page = new PageImpl<>(locations, pageable, totalElements);

        String hostName = "http://localhost";
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        Mockito.when(locationService.listByPage(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyMap()
        )).thenReturn(page);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._links.self.href", containsString(hostName + requestURI)))
                .andExpect(jsonPath("$._links.first").doesNotExist())
                .andExpect(jsonPath("$._links.next").doesNotExist())
                .andExpect(jsonPath("$._links.prev").doesNotExist())
                .andExpect(jsonPath("$._links.last").doesNotExist())
                .andDo(print());
    }

    @Test
    void testListPaginationLinkFirstPage() throws Exception {
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
        location1.setCode("LACA_US");
        location1.setCityName("New York City");
        location1.setRegionName("New York");
        location1.setCountryCode("US");
        location1.setCountryName("United States of America");
        location1.setEnabled(true);

        Location location4 = new Location();
        location2.setCode("MADRID_ES");
        location2.setCityName("CHILLAN");
        location2.setRegionName("ÑUBLE");
        location2.setCountryCode("CL");
        location2.setCountryName("CHILE");
        location2.setEnabled(true);


        List<Location> locations = List.of(location1, location2, location3, location4);

        int pageNum = 1;
        int pageSize = 3;
        String sortField = "code";
        int totalElements = locations.size();
        int totalPages = totalElements / pageSize + 1;

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Location> page = new PageImpl<>(locations, pageable, totalElements);

        String hostName = "http://localhost";
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        String nextPageURI = END_POINT_PATH + "?page=" + (pageNum + 1) + "&size=" + pageSize + "&sort=" + sortField;
        String lastPageURI = END_POINT_PATH + "?page=" + totalPages + "&size=" + pageSize + "&sort=" + sortField;

        Mockito.when(locationService.listByPage(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyMap()
        )).thenReturn(page);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._links.self.href", containsString(hostName + requestURI)))
                .andExpect(jsonPath("$._links.next.href", containsString(hostName + nextPageURI)))
                .andExpect(jsonPath("$._links.last.href", containsString(hostName + lastPageURI)))
                .andExpect(jsonPath("$._links.first").doesNotExist())
                .andExpect(jsonPath("$._links.prev").doesNotExist())
                .andDo(print());
    }

    @Test
    void testListPaginationLinkLastPage() throws Exception {
        int totalElements = 18;
        int pageSize = 5;
        List<Location> listLocations = new ArrayList<>(pageSize);

        for (int i = 1; i < pageSize;i++){
            listLocations.add(
                    new Location(
                            "CODE_" +i,
                            "City "+ i,
                            "Region Name",
                            "US",
                            "Country Name")
            );
        }

        int totalPages = (totalElements / pageSize + 1);
        int pageNum = totalPages;
        String sortField = "code";

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);

        String hostName = "http://localhost";
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        String firstPageURI = END_POINT_PATH + "?page=" + 1 + "&size=" + pageSize + "&sort=" + sortField;
        String prevPageURI = END_POINT_PATH + "?page=" + (pageNum -1) + "&size=" + pageSize + "&sort=" + sortField;

        Mockito.when(locationService.listByPage(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyMap()
        )).thenReturn(page);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._links.self.href", containsString(hostName + requestURI)))
                .andExpect(jsonPath("$._links.first.href", containsString(hostName + firstPageURI)))
                .andExpect(jsonPath("$._links.prev.href", containsString(hostName + prevPageURI)))
                .andExpect(jsonPath("$._links.last").doesNotExist())
                .andExpect(jsonPath("$._links.next").doesNotExist())
                .andDo(print());
    }

    @Test
    void testListPaginationLinksMiddlePage() throws Exception {
        int totalElements = 18;
        int pageSize = 5;
        List<Location> listLocations = new ArrayList<>(pageSize);

        for (int i = 1; i < pageSize;i++){
            listLocations.add(
                    new Location(
                            "CODE_" +i,
                            "City "+ i,
                            "Region Name",
                            "US",
                            "Country Name")
            );
        }

        int totalPages = (totalElements / pageSize + 1);
        int pageNum = 3;
        String sortField = "code";

        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Location> page = new PageImpl<>(listLocations, pageable, totalElements);

        String hostName = "http://localhost";
        String requestURI = END_POINT_PATH + "?page=" + pageNum + "&size=" + pageSize + "&sort=" + sortField;

        String firstPageURI = END_POINT_PATH + "?page=" + 1 + "&size=" + pageSize + "&sort=" + sortField;
        String prevPageURI = END_POINT_PATH + "?page=" + (pageNum - 1) + "&size=" + pageSize + "&sort=" + sortField;
        String nextPageURI = END_POINT_PATH + "?page=" + (pageNum + 1) + "&size=" + pageSize + "&sort=" + sortField;
        String lastPageURI = END_POINT_PATH + "?page=" + totalPages + "&size=" + pageSize + "&sort=" + sortField;

        Mockito.when(locationService.listByPage(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.anyMap()
        )).thenReturn(page);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._links.self.href", containsString(hostName + requestURI)))
                .andExpect(jsonPath("$._links.next.href", containsString(hostName + nextPageURI)))
                .andExpect(jsonPath("$._links.last.href", containsString(hostName + lastPageURI)))
                .andExpect(jsonPath("$._links.prev.href", containsString(hostName + prevPageURI)))
                .andExpect(jsonPath("$._links.first.href", containsString(hostName + firstPageURI)))
                .andDo(print());
    }

    @Test
    void testGetShouldReturn405NotAllowed() throws Exception {
        mockMvc
                .perform(
                        post(END_POINT_PATH + "/ASD"))
                .andExpect(status().isMethodNotAllowed())
                .andDo(print());
    }

    @Test
    void testGetLocation404NotFound() throws Exception {
        String locationCode = "ASD";
        String requestUri = END_POINT_PATH +"/"+ locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        Mockito.when(locationService.getLocation(locationCode)).thenThrow(ex);
        mockMvc.perform(get(requestUri))
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
        Mockito.when(locationService.getLocation(code)).thenReturn(location1);
        mockMvc
                .perform(
                        get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());
    }


    @Test
    void testUpdateShouldReturn404NotFound() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCode("ASDQWE");
        locationDTO.setCityName("New York City");
        locationDTO.setRegionName("New York");
        locationDTO.setCountryCode("US");
        locationDTO.setCountryName("United States of America");
        locationDTO.setEnabled(true);

        LocationNotFoundException ex = new LocationNotFoundException(locationDTO.getCityName());
        Mockito.when(locationService.update(Mockito.any())).thenThrow(ex);

        String bodyContent = mapper.writeValueAsString(locationDTO);
        mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testUpdateShouldReturn400BadRequest() throws Exception {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCityName("New York City");
        locationDTO.setRegionName("New York");
        locationDTO.setCountryCode("US");
        locationDTO.setCountryName("United States of America");
        locationDTO.setEnabled(true);

        String bodyContent = mapper.writeValueAsString(locationDTO);
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
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCode(location.getCode());
        locationDTO.setCityName(location.getCityName());
        locationDTO.setRegionName(location.getRegionName());
        locationDTO.setCountryCode(location.getCountryCode());
        locationDTO.setCountryName(location.getCountryName());
        locationDTO.setEnabled(location.isEnabled());

        Mockito.when(locationService.update(location)).thenReturn(location);

        String bodyContent = mapper.writeValueAsString(locationDTO);
        mockMvc
                .perform(
                        put(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.code", is("NYC_USA")))
                .andExpect(jsonPath("$.city_name", is("New York City")))
                .andDo(print());
    }

    @Test
    void testDelete404NotFound() throws Exception {
        String code = "NYC_US";
        String requestURI = END_POINT_PATH + "/" + code;

        Mockito.doThrow(LocationNotFoundException.class).when(locationService).delete(code);
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

        Mockito.doNothing().when(locationService).delete(code);
        mockMvc
                .perform(
                        delete(requestURI)
                                .contentType("application/json")
                ).andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void testValidateRequestBodyLocationCodeNotNull() throws Exception {
        LocationDTO location = new LocationDTO();
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