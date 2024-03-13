package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.DailyWeatherId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DailyWeatherRepository extends CrudRepository<DailyWeather, DailyWeatherId> {

    @Query("""
                SELECT d FROM DailyWeather d WHERE d.id.location.code = ?1 AND d.id.location.trashed = false
            """)
    List<DailyWeather> findByLocationCode(String locationCode);
}
