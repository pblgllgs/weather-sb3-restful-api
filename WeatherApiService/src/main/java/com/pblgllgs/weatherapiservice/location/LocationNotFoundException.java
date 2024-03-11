package com.pblgllgs.weatherapiservice.location;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String locationCode) {
        super("Location not found with code: "+locationCode);
    }

    public LocationNotFoundException(String locationCode,String cityName) {
        super("Location not found with code: "+locationCode+ " and city name: "+cityName);
    }
}
