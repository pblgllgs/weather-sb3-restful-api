package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApiServiceApplication.class, args);
    }

    @Bean
    public ModelMapper mapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TypeMap<HourlyWeather, HourlyWeatherDTO> typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
        typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);
        TypeMap<HourlyWeatherDTO, HourlyWeather> typeMap2 =
                mapper
                        .typeMap(
                                HourlyWeatherDTO.class,
                                HourlyWeather.class
                        );
        typeMap2.addMapping(HourlyWeatherDTO::getHourOfDay, (dest, value) -> dest.getId().setHourOfDay(value != null ?(int) value : 0));

        TypeMap<DailyWeather, DailyWeatherDTO> typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
        typeMap3.addMapping(src -> src.getId().getDayOfMonth(),DailyWeatherDTO::setDayOfMonth);
        typeMap3.addMapping(src -> src.getId().getMonth(),DailyWeatherDTO::setMonth);

        TypeMap<DailyWeatherDTO,DailyWeather> typeMap4 = mapper.typeMap(DailyWeatherDTO.class,DailyWeather.class);
        typeMap4.addMapping(DailyWeatherDTO::getDayOfMonth,DailyWeather::dayOfMonth);
        typeMap4.addMapping(DailyWeatherDTO::getMonth,DailyWeather::month);

        return mapper;
    }

}
