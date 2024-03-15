package com.pblgllgs.weatherapiservice.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "realtime_weather")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealtimeWeather {

    @Id
    @Column(
            name = "location_code"
    )
    @JsonIgnore
    private String locationCode;
    private int temperature;
    private int humidity;
    private int precipitation;
    @JsonProperty("wind_speed")
    private int windSpeed;

    @JsonProperty("last_updated")
    @JsonIgnore
    private Date lastUpdated;
    @Column(length = 50)
    private String status;

    @OneToOne
    @JoinColumn(
            name = "location_code"
    )
    @MapsId
    @JsonIgnore
    private Location location;

    public void setLocation(Location location) {
        this.locationCode = location.getCode();
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealtimeWeather that = (RealtimeWeather) o;
        return Objects.equals(locationCode, that.locationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationCode);
    }
}
