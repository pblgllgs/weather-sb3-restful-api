package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.HourlyWeather;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class LocationRepositoryTests {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testAddSuccess() {
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        Location locationSaved = locationRepository.save(location);
        assertThat(locationSaved).isNotNull();
        assertThat(locationSaved.getCode())
                .isEqualTo("NYC_USA");
    }

    @Test
    void testAdd2Success() {
        Location location = new Location();
        location.setCode("MBMH_IN");
        location.setCityName("Mumbai");
        location.setRegionName("Maharashtra");
        location.setCountryCode("IN");
        location.setCountryName("India");
        location.setEnabled(true);
        Location locationSaved = locationRepository.save(location);
        assertThat(locationSaved).isNotNull();
        assertThat(locationSaved.getCode())
                .isEqualTo("India");
    }

    @Test
    void testListSuccess() {
        List<Location> locations = locationRepository.findUntrashed();
        assertThat(locations).isNotEmpty();
        locations.forEach(System.out::println);
    }

    @Test
    void testGetNotFound404() {
        String code = "ASD";
        Location op = locationRepository.findByCode(code);
        assertThat(op).isNull();
    }

    @Test
    void testGetSuccess200() {
        String code = "NYC_USA";
        Location lo = locationRepository.findByCode(code);
        assertThat(lo).isNotNull();
    }

    @Test
    void testTrashed200() {
        String code = "NYC_USA";
        locationRepository.trashByCode(code);
        Location location = locationRepository.findByCode(code);
        assertThat(location).isNull();
    }

    @Test
    void testAddRealtimeWeatherData() {
        String code = "CH_CL";
        Location location = locationRepository.findByCode(code);
        RealtimeWeather realtimeWeather = location.getRealtimeWeather();

        if (realtimeWeather == null) {
            realtimeWeather = new RealtimeWeather();
            realtimeWeather.setLocation(location);
            location.setRealtimeWeather(realtimeWeather);
        }

        realtimeWeather.setTemperature(10);
        realtimeWeather.setHumidity(60);
        realtimeWeather.setPrecipitation(70);
        realtimeWeather.setStatus("Sunny");
        realtimeWeather.setWindSpeed(10);
        realtimeWeather.setLastUpdated(new Date());

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);

    }

    @Test
    void testHourlyWeatherData() {
        Location location = locationRepository.findById("MBMH_IN").get();
        List<HourlyWeather> listHourlyWeathers = location.getListHourlyWeathers();
        HourlyWeather forecast1 = new HourlyWeather().id(location, 8)
                .temperature(20)
                .precipitation(60)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather().location(location).hourOfDay(9)
                .temperature(20)
                .precipitation(60)
                .status("Cloudy");

        listHourlyWeathers.add(forecast1);
        listHourlyWeathers.add(forecast2);

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getListHourlyWeathers()).isNotEmpty();

    }

    @Test
    void testHourlyWeatherData2() {
        Location location = locationRepository.findById("CL").get();
        List<HourlyWeather> listHourlyWeathers = location.getListHourlyWeathers();
        HourlyWeather forecast1 = new HourlyWeather().id(location, 8)
                .temperature(10)
                .precipitation(80)
                .status("Cloudy");

        HourlyWeather forecast2 = new HourlyWeather().location(location).hourOfDay(9)
                .temperature(15)
                .precipitation(70)
                .status("Cloudy");

        listHourlyWeathers.add(forecast1);
        listHourlyWeathers.add(forecast2);

        Location updatedLocation = locationRepository.save(location);

        assertThat(updatedLocation.getListHourlyWeathers()).isNotEmpty();

    }

    @Test
    void testFindLocationByCountryCodeAndCityNameFound() {
        String countryCode = "US";
        String cityName = "New York City";
        Location lo = locationRepository.findByCountryCodeAndCityName(countryCode,cityName);
        assertThat(lo).isNotNull();
        assertThat(lo.getCountryCode()).isEqualTo(countryCode);
        assertThat(lo.getCityName()).isEqualTo(cityName);
    }

    @Test
    void testFindLocationByCountryCodeAndCityNameNotFound() {
        String countryCode = "ASD";
        String cityName = "TEST";
        Location lo = locationRepository.findByCountryCodeAndCityName(countryCode,cityName);
        assertThat(lo).isNull();
    }
}