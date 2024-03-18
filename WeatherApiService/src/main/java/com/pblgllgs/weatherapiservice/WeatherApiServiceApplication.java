package com.pblgllgs.weatherapiservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import com.pblgllgs.weatherapiservice.common.HourlyWeather;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.common.RealtimeWeather;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.full.FullWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;
import org.modelmapper.ModelMapper;
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

        configureMappingForHourlyWeather(mapper);

        configureMappingForDailyWeather(mapper);

        configureMappingForFullWeather(mapper);

        configureMappingForRealtimeWeather(mapper);

        return mapper;
    }

    private void configureMappingForRealtimeWeather(ModelMapper mapper) {
        mapper.typeMap(RealtimeWeatherDTO.class, RealtimeWeather.class)
                .addMappings(m -> m.skip(RealtimeWeather::setLocation));
    }

    private void configureMappingForFullWeather(ModelMapper mapper) {
        mapper.typeMap(Location.class, FullWeatherDTO.class)
                .addMapping(src -> src.toString(), FullWeatherDTO::setLocation);
    }

    private void configureMappingForDailyWeather(ModelMapper mapper) {
        mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class)
                .addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO::setDayOfMonth)
                .addMapping(src -> src.getId().getMonth(), DailyWeatherDTO::setMonth);

        mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class)
                .addMapping(src ->
                        src.getDayOfMonth(),
                        (dest, value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0)
                )
                .addMapping(src ->
                        src.getMonth(),
                        (dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0)
                );
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
    }

    private void configureMappingForHourlyWeather(ModelMapper mapper) {
        mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class)
                .addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO::setHourOfDay);
        mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class)
                .addMapping(src -> src.getHourOfDay(),
                        (dest, value) -> dest.getId().setHourOfDay(value != null ? (int) value : 0)
                );
    }

}
