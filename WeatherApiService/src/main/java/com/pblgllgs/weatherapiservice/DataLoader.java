package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import java.util.Date;


@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final RealtimeWeatherRepository realtimeWeatherRepository;

    @Override
    public void run(String... args) {
        if (locationRepository.findByCode("NYC_US") == null) {

            Location location = new Location();
            location.setCode("NYC_US");
            location.setCityName("New York City");
            location.setRegionName("New York");
            location.setCountryCode("US");
            location.setCountryName("United States of America");
            location.setEnabled(true);
            locationRepository.save(location);
        }

        if (locationRepository.findByCode("NYC_US") != null) {
            Location location = locationRepository.findByCode("NYC_US");
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

            realtimeWeatherRepository.save(realtimeWeather);
            location.setRealtimeWeather(realtimeWeather);
            locationRepository.save(location);
        }
    }
}
