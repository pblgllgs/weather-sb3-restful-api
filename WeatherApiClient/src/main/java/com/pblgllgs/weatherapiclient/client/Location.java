package com.pblgllgs.weatherapiclient.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Location
        (
                String code,
                @JsonProperty("city_name")
                String cityName,
                @JsonProperty("region_name")
                String regionName,
                @JsonProperty("country_name")
                String countryName,
                @JsonProperty("country_code")
                String countryCode,
                boolean enabled) {
}
