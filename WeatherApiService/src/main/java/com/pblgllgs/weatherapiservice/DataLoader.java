package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;


public class DataLoader implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final RealtimeWeatherRepository realtimeWeatherRepository;

    public DataLoader(LocationRepository locationRepository, RealtimeWeatherRepository realtimeWeatherRepository) {
        this.locationRepository = locationRepository;
        this.realtimeWeatherRepository = realtimeWeatherRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (locationRepository.findByCode("NYC_USA") == null) {

            Location location = new Location();
            location.setCode("NYC_USA");
            location.setCityName("New York City");
            location.setRegionName("New York");
            location.setCountryCode("US");
            location.setCountryName("United States of America");
            location.setEnabled(true);
            locationRepository.save(location);

            Location location2 = new Location();
            location2.setCode("CH_CL");
            location2.setCityName("CHILLAN");
            location2.setRegionName("ÑUBLE");
            location2.setCountryCode("CL");
            location2.setCountryName("CHILE");
            location2.setEnabled(true);
            locationRepository.save(location2);

        }

        if (locationRepository.findByCode("NYC_USA") != null && realtimeWeatherRepository.findById("NYC_USA").isEmpty()) {
            String code = "NYC_USA";
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

            locationRepository.save(location);
        }

        if (locationRepository.findByCode("CH_CL") != null && realtimeWeatherRepository.findById("CH_CL").isEmpty()) {
            String code = "CH_CL";
            Location location = locationRepository.findByCode(code);
            RealtimeWeather realtimeWeather = location.getRealtimeWeather();

            if (realtimeWeather == null) {
                realtimeWeather = new RealtimeWeather();
                realtimeWeather.setLocation(location);
                location.setRealtimeWeather(realtimeWeather);
            }

            realtimeWeather.setTemperature(-1);
            realtimeWeather.setHumidity(32);
            realtimeWeather.setPrecipitation(40);
            realtimeWeather.setStatus("Snowy");
            realtimeWeather.setWindSpeed(15);
            realtimeWeather.setLastUpdated(new Date());

            locationRepository.save(location);
        }

        }
    }
