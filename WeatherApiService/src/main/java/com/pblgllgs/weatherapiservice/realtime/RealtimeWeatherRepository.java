package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RealtimeWeatherRepository extends CrudRepository<RealtimeWeather, String> {

    @Query("SELECT r FROM RealtimeWeather  r WHERE r.location.countryCode =?1 AND r.location.cityName=?2")
    RealtimeWeather findByCountryCodeAndCity(String countryCode, String cityName);

    @Query("SELECT r FROM RealtimeWeather  r WHERE r.locationCode =?1 AND r.location.trashed=false")
    RealtimeWeather findByLocationCode(String locationCode);
}
