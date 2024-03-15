package com.pblgllgs.weatherapiservice.full;

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */
@Service
@RequiredArgsConstructor
public class FullWeatherService {

    private final LocationRepository locationRepository;

    public Location getByLocation(Location locationFromIp) {
        String cityName = locationFromIp.getCityName();
        String countryCode = locationFromIp.getCountryCode();
        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }
        return locationInDB;
    }

    public Location getByLocationCode(String locationFromCode) {
        Location locationInDB = locationRepository.findByCode(locationFromCode);
        if (locationInDB == null) {
            throw new LocationNotFoundException(locationFromCode);
        }
        return locationInDB;
    }

    public Location update(String locationCode, Location locationInRequest) {
        Location locationInDB = locationRepository.findByCode(locationCode);
        if (locationInDB == null) {
            throw new LocationNotFoundException(locationCode);
        }
        RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
        realtimeWeather.setLocation(locationInDB);
        realtimeWeather.setLastUpdated(new Date());

        if (locationInDB.getRealtimeWeather() == null){
            locationInDB.setRealtimeWeather(realtimeWeather);
            locationRepository.save(locationInDB);
        }

        List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
        listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));

        List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
        listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDB));

        locationInRequest.setCode(locationInDB.getCode());
        locationInRequest.setCityName(locationInDB.getCityName());
        locationInRequest.setRegionName(locationInDB.getRegionName());
        locationInRequest.setCountryCode(locationInDB.getCountryCode());
        locationInRequest.setCountryName(locationInDB.getCountryName());
        locationInRequest.setEnabled(locationInDB.isEnabled());
        locationInRequest.setTrashed(locationInDB.isTrashed());

        return locationRepository.save(locationInRequest);
    }


}
