package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repository;


    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public Location add(Location location) {
        return repository.save(location);
    }

    public List<Location> list() {
        return repository.findUntrashed();
    }

    public Location getLocation(String code) {
        return repository.findByCode(code);
    }

    public Location update(Location location) {
        String code =  location.getCode();
        Location lo = this.getLocation(code);
        if (lo == null) {
            throw new LocationNotFoundException("Location not found");
        }
        lo.setCountryName(location.getCountryName());
        lo.setCountryCode(location.getCountryCode());
        lo.setRegionName(location.getRegionName());
        lo.setCityName(location.getCityName());
        lo.setEnabled(location.isEnabled());
        return repository.save(lo);
    }
}
