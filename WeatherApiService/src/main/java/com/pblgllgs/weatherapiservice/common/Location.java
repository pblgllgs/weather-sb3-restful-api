package com.pblgllgs.weatherapiservice.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "locations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @Column(length = 12, nullable = false, unique = true)
    private String code;

    @JsonProperty("city_name")
    @Column(length = 128, nullable = false, name = "city_name")
    private String cityName;

    @JsonProperty("region_name")
    @Column(length = 128, name = "region_name")
    private String regionName;

    @JsonProperty("country_name")
    @Column(length = 64, nullable = false, name = "country_name")
    private String countryName;

    @JsonProperty("country_code")
    @Column(length = 2, nullable = false, name = "country_code")
    private String countryCode;

    @OneToOne(
            fetch = FetchType.EAGER,
            mappedBy = "location",
            cascade = CascadeType.ALL
    )
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private RealtimeWeather realtimeWeather;
    private boolean enabled;
    @JsonIgnore
    private boolean trashed;
    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HourlyWeather> listHourlyWeather = new ArrayList<>();

    @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyWeather> listDailyWeather = new ArrayList<>();

    public Location(String cityName, String regionName, String countryName, String countryCode) {
        this.cityName = cityName;
        this.regionName = regionName;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }


    public void copyFieldsFrom(Location another) {
        setCityName(another.getCityName());
        setRegionName(another.getRegionName());
        setCountryCode(another.getCountryCode());
        setCountryName(another.getCountryName());
        setEnabled(another.isEnabled());
    }

    public void copyAllFieldsFrom(Location another) {
        copyFieldsFrom(another);
        setCode(another.getCode());
        setTrashed(another.isTrashed());
    }

    public Location code(String code) {
        setCode(code);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(code, location.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " => " + this.cityName + ", " + (regionName != null ? regionName : "") + ", " + countryName;
    }
}
