package com.pblgllgs.weatherapiservice;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.pblgllgs.weatherapicommon.common.Location;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeolocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private static final String DBPATH = "WeatherApiService/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private final IP2Location ipLocator = new IP2Location();

    public GeolocationService() {
        try {
            ipLocator.Open(DBPATH);
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    public Location getLocation(String ipAddress) throws IOException, GeolocationException {
        try {
            IPResult result = ipLocator.IPQuery(ipAddress);
            LOGGER.info(result.toString());
            if (!"OK".equals(result.getStatus())) {
                throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
            }
            Location location = new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
            return location;
        } catch (IOException ex) {
            throw new GeolocationException("Error querying IP database");
        }
    }

}
