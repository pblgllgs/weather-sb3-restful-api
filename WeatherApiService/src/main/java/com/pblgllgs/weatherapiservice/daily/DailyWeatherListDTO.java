package com.pblgllgs.weatherapiservice.daily;
/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pblgllgs.weatherapiservice.common.DailyWeather;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyWeatherListDTO extends RepresentationModel<DailyWeatherListDTO> {

    private String location;
    @JsonProperty("daily_forecast")
    private List<DailyWeatherDTO> dailyForecast = new ArrayList<>();

    public void addDailyWeatherDTO(DailyWeatherDTO dto){
        this.dailyForecast.add(dto);
    }
}
