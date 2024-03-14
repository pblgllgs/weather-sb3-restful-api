package com.pblgllgs.weatherapiservice.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeWeatherDTO {
    private String location;
    @Range(min = -50,max = 50, message = "Temperature must be in the range -50 to 50 C°")
    private int temperature;
    @Range(min = 0,max = 100, message = "Humidity must be in the range 0 to 100 percentage")
    private int humidity;
    @Range(min = 0,max = 100, message = "Precipitation must be in the range 0 to 100 percentage")
    private int precipitation;
    @JsonProperty("wind_speed")
    @Range(min = 0,max = 200, message = "Wind speed must be in the range 0 to 200 km/h")
    private int windSpeed;
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;
    @Column(length = 50)
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3,max = 50, message = "Status must be in between 3-50 characters")
    private String status;
}
