package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
            throw new LocationNotFoundException(location.getCountryCode(), location.getCityName());
        }
        return hourlyWeatherRepository.findByLocationCode(byCountryCodeAndCityName.getCode(), hour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int hour){
        Location location = locationRepository.findByCode(locationCode);
        if (location ==  null){
            throw new LocationNotFoundException(locationCode);
        }
        return hourlyWeatherRepository.findByLocationCode(location.getCode(), hour);
    }


    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherInRequest) throws LocationNotFoundException {
        Location location = locationRepository.findByCode(locationCode);
        if (location == null){
            throw new LocationNotFoundException(locationCode);
        }
        for (HourlyWeather item: hourlyWeatherInRequest) {
            item.getId().setLocation(location);
        }
        List<HourlyWeather> hourlyWeatherDB = location.getListHourlyWeathers();
        List<HourlyWeather> hourlyWeathersToBeRemoved =  new ArrayList<>();

        for (HourlyWeather item :  hourlyWeatherDB) {
            if (!hourlyWeatherInRequest.contains(item)){
                hourlyWeathersToBeRemoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : hourlyWeathersToBeRemoved){
            hourlyWeatherDB.remove(item);
        }

        return (List<HourlyWeather>) hourlyWeatherRepository.saveAll(hourlyWeatherInRequest);
    }
}
