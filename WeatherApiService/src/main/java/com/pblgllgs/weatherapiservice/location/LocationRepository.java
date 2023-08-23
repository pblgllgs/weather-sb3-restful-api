package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends CrudRepository<Location, String> {
}