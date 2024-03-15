package com.pblgllgs.weatherapiservice.common;
/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "weather_daily")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyWeather {

    @EmbeddedId
    private DailyWeatherId id = new DailyWeatherId();
    @Column(name = "min_temp")
    private int minTemp;
    @Column(name = "max_temp")
    private int maxTemp;
    private int precipitation;
    @Column(length = 50)
    private String status;

    public DailyWeather getShallowCopy() {
        DailyWeather copy = new DailyWeather();
        copy.setId(this.getId());
        return copy;
    }

    public DailyWeather precipitation(int precipitation) {
        this.setPrecipitation(precipitation);
        return this;
    }

    public DailyWeather status(String status) {
        this.setStatus(status);
        return this;
    }

    public DailyWeather location(Location location) {
        this.id.setLocation(location);
        return this;
    }

    public DailyWeather dayOfMonth(int day) {
        this.id.setDayOfMonth(day);
        return this;
    }

    public DailyWeather month(int month) {
        this.id.setMonth(month);
        return this;
    }

    public DailyWeather minTemp(int temp) {
        setMinTemp(temp);
        return this;
    }

    public DailyWeather maxTemp(int temp) {
        setMaxTemp(temp);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DailyWeather other = (DailyWeather) obj;
        return Objects.equals(id, other.id);
    }
}
