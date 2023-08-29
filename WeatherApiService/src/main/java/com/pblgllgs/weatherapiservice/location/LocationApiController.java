package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

    private final LocationService locationService;
    private final ModelMapper mapper;

    public LocationApiController(LocationService locationService, ModelMapper mapper) {
        this.locationService = locationService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@Valid @RequestBody LocationDTO locationDTO) {
        Location addedLocation = locationService.add(dtoToEntity(locationDTO));
        URI uri = URI.create("/v1/locations/" + locationDTO.getCode());
        return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> listLocation() {
        List<Location> list = locationService.list();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listEntity2ListDTO(list));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Object> getLocation(@PathVariable("code") String code) {
        Location location = locationService.getLocation(code);
        return ResponseEntity.ok(entity2DTO(location));
    }

    @PutMapping
    public ResponseEntity<Object> updateLocation(@Valid @RequestBody LocationDTO locationDTO) {
        Location updateLocation = locationService.update(dtoToEntity(locationDTO));
        return ResponseEntity.ok(entity2DTO(updateLocation));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Object> deleteLocation(@PathVariable("code") String code) {
        locationService.delete(code);
        return ResponseEntity.noContent().build();
    }

    private LocationDTO entity2DTO(Location location) {
        return mapper.map(location, LocationDTO.class);
    }

    private Location dtoToEntity(LocationDTO locationDTO) {
        return mapper.map(locationDTO, Location.class);
    }

    private List<LocationDTO> listEntity2ListDTO(List<Location> locations) {
        return locations.stream().map(this::entity2DTO).toList();
    }

}
