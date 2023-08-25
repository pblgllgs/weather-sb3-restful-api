package com.pblgllgs.weatherapiservice.realtime;

import com.pblgllgs.weatherapicommon.common.RealtimeWeather;
import org.springframework.data.repository.CrudRepository;

public interface RealtimeWeatherRepository extends CrudRepository<RealtimeWeather,String> {
}
