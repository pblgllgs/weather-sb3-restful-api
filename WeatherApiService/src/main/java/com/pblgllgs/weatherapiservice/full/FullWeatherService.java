package com.pblgllgs.weatherapiservice.full;

import com.pblgllgs.weatherapiservice.AbstractLocationService;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
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
public class FullWeatherService extends AbstractLocationService {
    public FullWeatherService(LocationRepository locationRepository) {
        super();
        this.locationRepository = locationRepository;
    }

    public Location getByLocation(Location locationFromIp) {
        String cityName = locationFromIp.getCityName();
        String countryCode = locationFromIp.getCountryCode();
        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
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

        setLocationForWeatherData(locationInRequest, locationInDB);

        saveRealtimeWeatherIfNotExistsBefore(locationInRequest, locationInDB);

        locationInRequest.copyAllFieldsFrom(locationInDB);

        return locationRepository.save(locationInRequest);
    }

    private void saveRealtimeWeatherIfNotExistsBefore(Location locationInRequest, Location locationInDB) {
        if (locationInDB.getRealtimeWeather() == null) {
            locationInDB.setRealtimeWeather(locationInRequest.getRealtimeWeather());
            locationRepository.save(locationInDB);
        }
    }

    private static void setLocationForWeatherData(Location locationInRequest, Location locationInDB) {
        RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
        realtimeWeather.setLocation(locationInDB);
        realtimeWeather.setLastUpdated(new Date());

        List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
        listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));

        List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
        listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDB));
    }


}
