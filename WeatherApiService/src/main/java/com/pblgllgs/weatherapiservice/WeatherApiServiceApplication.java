package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapicommon.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = "com.pblgllgs.weatherapicommon.common")
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
        typeMap2.addMapping(src -> src.getHourOfDay(), (dest, value) -> dest.getId().setHourOfDay(value != null ?(int) value : 0));

        return mapper;
    }

}
