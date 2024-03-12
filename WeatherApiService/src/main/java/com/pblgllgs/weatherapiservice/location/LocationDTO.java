package com.pblgllgs.weatherapiservice.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class LocationDTO {
    @NotNull(message = "Location code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must ve 3-12 characters")
    private String code;
    @JsonProperty("city_name")
    @NotNull(message = "City name cannot be null")
    @Length(min = 3, max = 128, message = "City name must ve 3-128 characters")
    private String cityName;
    @JsonProperty("region_name")
    @Length(min = 3, max = 128, message = "Region name must ve 3-128 characters")
    private String regionName;
    @JsonProperty("country_name")
    @NotNull(message = "Country name cannot be null")
    @Length(min = 3, max = 64, message = "Country name must ve 3-64 characters")
    private String countryName;
    @JsonProperty("country_code")
    @NotNull(message = "Country code cannot be null")
    @Length(min = 2, max = 2, message = "Country code must be 2 characters")
    private String countryCode;
    private boolean enabled;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
