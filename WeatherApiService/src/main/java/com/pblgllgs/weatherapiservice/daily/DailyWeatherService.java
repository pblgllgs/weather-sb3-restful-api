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

    public List<DailyWeather> getByLocation(Location location) {

        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return dailyWeatherRepository.findByLocationCode(locationInDB.getCode());
    }

    public List<DailyWeather> getByLocationCode(String locationCode) {
        Location location = locationRepository.findByCode(locationCode);
        if (location == null) {
            throw new LocationNotFoundException(locationCode);
        }
        return dailyWeatherRepository.findByLocationCode(locationCode);
    }

    public DailyWeather save(DailyWeatherDTO dto, String locationCode) {
        Location location = locationRepository.findByCode(locationCode);
        DailyWeather dailyWeather = new DailyWeather()
                .location(location)
                .maxTemp(dto.getMaxTemp())
                .minTemp(dto.getMinTemp())
                .dayOfMonth(dto.getDayOfMonth())
                .month(dto.getMonth())
                .precipitation(dto.getPrecipitation())
                .status(dto.getStatus());
        return dailyWeatherRepository.save(dailyWeather);
    }
}
