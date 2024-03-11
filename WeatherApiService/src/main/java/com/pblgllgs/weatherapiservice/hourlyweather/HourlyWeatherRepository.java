package com.pblgllgs.weatherapiservice.hourlyweather;

import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeatherId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {

    @Query("""
                SELECT h
                FROM HourlyWeather h
                WHERE h.id.location.code = ?1 
                AND h.id.hourOfDay > ?2
                AND h.id.location.trashed =false
            """)
    List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}
