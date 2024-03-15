package com.pblgllgs.weatherapiservice.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HourlyWeatherId implements Serializable {
    private int hourOfDay;
    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourlyWeatherId that = (HourlyWeatherId) o;
        return hourOfDay == that.hourOfDay && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hourOfDay, location);
    }
}
