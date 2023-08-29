package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<Location> list() {
        return repository.findUntrashed();
    }

    @Transactional(readOnly = true)
    public Location getLocation(String code) throws LocationNotFoundException {
        Location location = repository.findByCode(code);
        if (location == null){
            throw new LocationNotFoundException(code);
        }
        return location;
    }

    @Transactional
    public Location update(Location locationInRequest) {
        String code = locationInRequest.getCode();
        Location locationInDB = this.getLocation(code);
        if (locationInDB == null) {
            throw new LocationNotFoundException(code);
        }
        locationInDB.setCountryName(locationInRequest.getCountryName());
        locationInDB.setCountryCode(locationInRequest.getCountryCode());
        locationInDB.setRegionName(locationInRequest.getRegionName());
        locationInDB.setCityName(locationInRequest.getCityName());
        locationInDB.setEnabled(locationInRequest.isEnabled());
        return repository.save(locationInDB);
    }

    @Transactional
    public void delete(String code) {
        Location location = this.getLocation(code);
        if (location == null) {
            throw new LocationNotFoundException(code);
        }
        repository.trashByCode(code);
    }
}
