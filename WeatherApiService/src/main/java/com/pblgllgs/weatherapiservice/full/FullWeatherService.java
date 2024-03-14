package com.pblgllgs.weatherapiservice.full;

import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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



}
