package com.pblgllgs.weatherapiservice.hourlyweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class HourlyWeatherListDTO  extends RepresentationModel<HourlyWeatherListDTO> {
    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> hourlyForecast =  new ArrayList<>();

    public void add(HourlyWeatherDTO hourlyWeatherDTO){
        this.hourlyForecast.add(hourlyWeatherDTO);
    }
}
