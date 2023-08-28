package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapicommon.common.HourlyWeather;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HourlyWeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HourlyWeatherService.class);

    private final HourlyWeatherRepository hourlyWeatherRepository;
    private final LocationRepository locationRepository;

    public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository, LocationRepository locationRepository) {
        this.hourlyWeatherRepository = hourlyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public List<HourlyWeather> getByLocation(Location location, int hour){
        Location byCountryCodeAndCityName = locationRepository.findByCountryCodeAndCityName(
                location.getCountryCode(),
                location.getCityName()
        );
        if (byCountryCodeAndCityName ==  null){
            throw new LocationNotFoundException("Location not found");
        }
        return hourlyWeatherRepository.findByLocationCode(byCountryCodeAndCityName.getCode(), hour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int hour){
        Location byCountryCodeAndCityName = locationRepository.findByCode(locationCode);
        if (byCountryCodeAndCityName ==  null){
            throw new LocationNotFoundException("Location not found");
        }
        return hourlyWeatherRepository.findByLocationCode(byCountryCodeAndCityName.getCode(), hour);
    }
}
