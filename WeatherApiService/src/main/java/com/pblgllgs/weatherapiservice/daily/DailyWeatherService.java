package com.pblgllgs.weatherapiservice.daily;
/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyWeatherService {

    private final DailyWeatherRepository dailyWeatherRepository;
    private final LocationRepository locationRepository;

    public List<DailyWeather> getByLocation(Location location){

        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode,cityName);

        if (locationInDB == null){
            throw new LocationNotFoundException(countryCode,cityName);
        }

        return dailyWeatherRepository.findByLocationCode(locationInDB.getCode());



    }
}
