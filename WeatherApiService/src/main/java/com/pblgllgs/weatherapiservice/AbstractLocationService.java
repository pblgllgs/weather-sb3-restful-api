package com.pblgllgs.weatherapiservice;
/*
 *
 * @author pblgl
 * Created on 15-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import com.pblgllgs.weatherapiservice.location.LocationRepository;

public abstract class AbstractLocationService {
    protected LocationRepository locationRepository;
    public Location getLocation(String code) {
        Location location = locationRepository.findByCode(code);
        if (location == null){
            throw new LocationNotFoundException(code);
        }
        return location;
    }
}
