package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.DailyWeatherId;
import com.pblgllgs.weatherapiservice.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class DailyWeatherRepositoryTests {
    @Autowired
    private DailyWeatherRepository dailyWeatherRepository;

    @Test
    void testAdd(){
        String locationCode = "NYC_US";
        Location location = new Location().code(locationCode);

        DailyWeather forecast = new DailyWeather()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(20)
                .status("Cloudy");

        DailyWeather addedForecast = dailyWeatherRepository.save(forecast);

        assertThat(addedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
    }

    @Test
    void testDelete(){
        String locationCode = "SCL";
        Location location = new Location().code(locationCode);
        DailyWeatherId  id = new DailyWeatherId(16,7,location);

        dailyWeatherRepository.deleteById(id);

        Optional<DailyWeather> dailyWeatherRepositoryById = dailyWeatherRepository.findById(id);

        assertThat(dailyWeatherRepositoryById).isNotPresent();

    }

    @Test
    void testFindByLocationCodeNotFound(){
        String locationCode = "ABC_XYZ";
        List<DailyWeather> listDailyWeather = dailyWeatherRepository.findByLocationCode(locationCode);
        assertThat(listDailyWeather).isEmpty();
    }

    @Test
    void testFindByLocationCodeFound(){
        String locationCode = "NYC_US";
        List<DailyWeather> listDailyWeather = dailyWeatherRepository.findByLocationCode(locationCode);
        assertThat(listDailyWeather).isNotEmpty();
    }
}
