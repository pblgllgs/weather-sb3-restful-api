package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class RealtimeWeatherRepositoryTests {

    @Autowired
    private RealtimeWeatherRepository realtimeWeatherRepository;

    @Test
    void testUpdate(){
        String locationCode = "NYC_USA";
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findById(locationCode).get();
        realtimeWeather.setTemperature(-1);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setPrecipitation(40);
        realtimeWeather.setStatus("Snowy");
        realtimeWeather.setWindSpeed(15);
        realtimeWeather.setLastUpdated(new Date());

        RealtimeWeather updatedRealtimeWeather = realtimeWeatherRepository.save(realtimeWeather);

        assertThat(updatedRealtimeWeather.getHumidity()).isEqualTo(32);
    }

    @Test
    void testFindByCountryCodeAndCityNotFound() {
        String countryCode = "123";
        String cityName = "ASD";

        RealtimeWeather updatedRealtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode,cityName);
        assertThat(updatedRealtimeWeather).isNull();
    }

    @Test
    void testFindByCountryCodeAndCityFound() {
        String countryCode = "NYC_USA";
        String cityName = "New York City";

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode,cityName);

        assertThat(realtimeWeather).isNotNull();
        assertThat(realtimeWeather.getLocation().getCityName()).isEqualTo(cityName);
    }
}
