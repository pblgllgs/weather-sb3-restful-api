package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RealtimeWeatherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherService.class);

    private final RealtimeWeatherRepository realtimeWeatherRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
    }

    public RealtimeWeather getByLocation(Location location){
        LOGGER.info(location.toString());
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);
        if(realtimeWeather == null){
            throw new LocationNotFoundException("Location not found with the given country code: "+ countryCode +" and city name: "+cityName);
        }
        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode){
        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);
        if(realtimeWeather == null){
            throw new LocationNotFoundException("Location not found with the given country code: "+ locationCode);
        }
        return realtimeWeather;
    }
}
