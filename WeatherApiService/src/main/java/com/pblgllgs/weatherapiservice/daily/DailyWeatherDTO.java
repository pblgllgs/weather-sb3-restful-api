package com.pblgllgs.weatherapiservice.daily;
/*
 *
 * @author pblgl
 * Created on 13-03-2024
 *
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"day_of_month", "month", "min_temp", "max_temp", "precipitation", "status"})
public class DailyWeatherDTO {
    @JsonProperty("day_of_month")
    @Range(min = 1, max = 31, message = "Day of the month must be between 1-31")
    private int dayOfMonth;
    @Range(min = 1, max = 12, message = "Month must be between 1-12")
    private int month;
    @Range(min = -50, max = 50, message = "Minimum temperature must be in the range od -50 to 50 Celsius degree")
    @JsonProperty("min_temp")
    private int minTemp;
    @Range(min = -50, max = 50, message = "Minimum temperature must be in the range od -50 to 50 Celsius degree")
    @JsonProperty("max_temp")
    private int maxTemp;
    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;
    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;
}
