package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.common.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<Location, String>, PagingAndSortingRepository<Location,String> {

    @Query("SELECT l FROM Location l WHERE l.trashed =false")
    @Deprecated
    List<Location> findUntrashed();

    @Query("SELECT l FROM Location l WHERE l.trashed =false")
    Page<Location> findUntrashed(Pageable pageable);

    @Query("SELECT l FROM Location l WHERE l.trashed =false AND l.code=?1")
    Location findByCode(String code);

    @Modifying
    @Query("UPDATE Location SET trashed = true WHERE code = ?1")
    void trashByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.trashed =false AND l.countryCode=?1 AND l.cityName = ?2")
    Location findByCountryCodeAndCityName(String countryCode, String cityName);
}