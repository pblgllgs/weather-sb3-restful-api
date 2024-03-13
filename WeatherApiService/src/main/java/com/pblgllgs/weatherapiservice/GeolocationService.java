package com.pblgllgs.weatherapiservice;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.pblgllgs.weatherapiservice.common.Location;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class GeolocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private static final String DBPATH = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
    private final IP2Location ipLocator = new IP2Location();

    public GeolocationService() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DBPATH);
            byte[] data = inputStream.readAllBytes();
            ipLocator.Open(data);
            inputStream.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    public Location getLocation(String ipAddress) throws IOException, GeolocationException {
        try {
            IPResult result = ipLocator.IPQuery(ipAddress);
            log.info(result.toString());
            if (!"OK".equals(result.getStatus())) {
                throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
            }
            return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
        } catch (IOException ex) {
            throw new GeolocationException("Error querying IP database");
        }
    }

}
