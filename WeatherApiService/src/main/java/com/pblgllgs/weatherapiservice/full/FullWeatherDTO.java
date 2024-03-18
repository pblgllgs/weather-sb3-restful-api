package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherDTO;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherDTO;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FullWeatherDTO extends RepresentationModel<FullWeatherDTO> {
    private String location;
    @JsonInclude(
            value = JsonInclude.Include.CUSTOM,
            valueFilter = RealtimeWeatherFieldFilter.class
    )
    @Valid
    private RealtimeWeatherDTO realtimeWeather = new RealtimeWeatherDTO();
    @JsonProperty("hourly_forecast")
    @Valid
    private List<HourlyWeatherDTO> listHourlyWeather = new ArrayList<>();
    @JsonProperty("daily_forecast")
    @Valid
    private List<DailyWeatherDTO> listDailyWeather = new ArrayList<>();
}
