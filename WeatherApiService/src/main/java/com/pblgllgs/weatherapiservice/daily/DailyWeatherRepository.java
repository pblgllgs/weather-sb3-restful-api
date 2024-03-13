package com.pblgllgs.weatherapiservice.daily;

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.DailyWeatherId;
import org.springframework.data.repository.CrudRepository;

public interface DailyWeatherRepository extends CrudRepository<DailyWeather, DailyWeatherId> {
}
