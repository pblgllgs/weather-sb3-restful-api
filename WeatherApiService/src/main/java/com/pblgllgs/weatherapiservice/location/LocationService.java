package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository repository;


    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public Location add(Location location){
        return repository.save(location);
    }
}
