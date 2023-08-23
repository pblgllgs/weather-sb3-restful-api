package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

    private final LocationService service;

    public LocationApiController(LocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(@Valid @RequestBody Location location){
        Location addedLocation = service.add(location);
        URI uri = URI.create("/v1/locations/"+ location.getCode());
        return ResponseEntity.created(uri).body(addedLocation);
    }

}
