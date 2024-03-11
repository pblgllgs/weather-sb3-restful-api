package com.pblgllgs.weatherapiservice.hourly;


import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeatherId;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class HourlyWeatherRepositoryTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(HourlyWeatherRepositoryTests.class);

    @Autowired
    private HourlyWeatherRepository hourlyWeatherRepository;

    @Test
    void testAdd() {
        String locationCode = "ARG";
        int hourOfDay = 12;

        Location location =  new Location().code(locationCode);

        HourlyWeather forecast =  new HourlyWeather()
                .location(location)
                .hourOfDay(hourOfDay)
                .temperature(16)
                .precipitation(50)
                .status("Cloudy");

        HourlyWeather updatedForecast = hourlyWeatherRepository.save(forecast);

        assertThat(updatedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
        assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
    }

    @Test
    void testDelete() {
        Location location = new Location().code("ARG");
        HourlyWeatherId id= new HourlyWeatherId(10 ,location);
        hourlyWeatherRepository.deleteById(id);
        Optional<HourlyWeather> result = hourlyWeatherRepository.findById(id);
        assertThat(result).isNotPresent();
    }

    @Test
    void testFindByLocationCodeFound() {
        String locationCode = "CL";
        int currentHour = 7;
        List<HourlyWeather> hourlyWeatherList = hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
        assertThat(hourlyWeatherList).isNotEmpty();
    }

    @Test
    void testFindByLocationCodeNotFound() {
        String locationCode = "MBMH_IN";
        int currentHour = 15;
        List<HourlyWeather> hourlyWeatherList = hourlyWeatherRepository.findByLocationCode(locationCode, currentHour);
        assertThat(hourlyWeatherList).isEmpty();
    }
}
