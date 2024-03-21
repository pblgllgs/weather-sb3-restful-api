package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.AbstractLocationService;
import com.pblgllgs.weatherapiservice.common.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return locationRepository.findUntrashed(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Location> listByPage(
            int pageNum,
            int pageSize,
            String sortOption,
            Map<String, Object> filterFields
    ) {
        Sort sort = createSorts(sortOption);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return locationRepository.listWithFilter(pageable, filterFields);
    }

    private Sort createSorts(String sortOption) {
        String[] sortFields = sortOption.split(",");
        Sort sort;
        if (sortFields.length > 1) {
            sort = createSingleSort(sortFields[0]);
            for (int i = 1; i < sortFields.length; i++) {
                sort = sort.and(createSingleSort(sortFields[i]));
            }
        } else {
            sort = createSingleSort(sortOption);
        }
        return sort;
    }

    private Sort createSingleSort(String fieldName) {
        String actualFieldName = fieldName.replace("-", "");
        return fieldName.startsWith("-") ? Sort.by(actualFieldName).descending() : Sort.by(actualFieldName).ascending();
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
