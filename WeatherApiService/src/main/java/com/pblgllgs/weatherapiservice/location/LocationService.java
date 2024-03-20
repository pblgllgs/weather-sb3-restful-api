package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.AbstractLocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pblgllgs.weatherapiservice.common.Location;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LocationService extends AbstractLocationService {

    public LocationService(LocationRepository locationRepository) {
        super();
        this.locationRepository = locationRepository;
    }

    public Location add(Location location) {
        return locationRepository.save(location);
    }
    @Deprecated
    @Transactional(readOnly = true)
    public List<Location> list() {
        return locationRepository.findUntrashed();
    }

    @Deprecated
    @Transactional(readOnly = true)
    public Page<Location> listByPage(int pageNum, int pageSize, String sortField) {
        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize,sort);
        return locationRepository.findUntrashed(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Location> listByPage(
            int pageNum,
            int pageSize,
            String sortField,
            Map<String, Object> filterFields
    ) {
        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize,sort);
        return locationRepository.listWithFilter(pageable,filterFields);
    }


    @Transactional
    public Location update(Location locationInRequest) {
        String code = locationInRequest.getCode();
        Location locationInDB = this.getLocation(code);
        if (locationInDB == null) {
            throw new LocationNotFoundException(code);
        }
        locationInDB.copyFieldsFrom(locationInRequest);
        return locationRepository.save(locationInDB);
    }

    @Transactional
    public void delete(String code) {
        Location location = this.getLocation(code);
        if (location == null) {
            throw new LocationNotFoundException(code);
        }
        locationRepository.trashByCode(code);
    }
}
