package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.GeolocationException;
import com.pblgllgs.weatherapiservice.common.Location;
import com.pblgllgs.weatherapiservice.daily.DailyWeatherApiController;
import com.pblgllgs.weatherapiservice.full.FullWeatherApiController;
import com.pblgllgs.weatherapiservice.hourlyweather.HourlyWeatherApiController;
import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherApiController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/locations")
@Validated
@RequiredArgsConstructor
public class LocationApiController {

    private final LocationService locationService;
    private final ModelMapper mapper;
    private final Map<String, String> propertyMap = Map.of(
            "code", "code",
            "city_name", "cityName",
            "region_name", "regionName",
            "country_name", "countryName",
            "country_code", "countryCode",
            "enabled", "enabled"
    );

    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@Valid @RequestBody LocationDTO locationDTO) throws GeolocationException, IOException {
        Location newLocation = locationService.add(dtoToEntity(locationDTO));
        URI uri = URI.create("/v1/locations/" + locationDTO.getCode());
        LocationDTO addedLocation = entity2DTO(newLocation);
        return ResponseEntity.created(uri).body(addLinksToItem(addedLocation));
    }

    @Deprecated
    public ResponseEntity<List<LocationDTO>> listLocation() {
        List<Location> list = locationService.list();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(listEntity2ListDTO(list));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<LocationDTO>> listLocation(
            @RequestParam(value = "page", required = false, defaultValue = "1")
            @Min(value = 1) Integer pageNum,
            @RequestParam(value = "size", required = false, defaultValue = "3")
            @Min(value = 3) @Max(value = 20) Integer pageSize,
            @RequestParam(value = "sort", required = false, defaultValue = "code") String sortOption,
            @RequestParam(value = "enabled", required = false, defaultValue = "") String enabled,
            @RequestParam(value = "region_name", required = false, defaultValue = "") String regionName,
            @RequestParam(value = "country_code", required = false, defaultValue = "") String countryCode
    ) throws BadRequestException {
        String[] sortFields = sortOption.split(",");
        if (sortFields.length > 1){
            for (int i = 0; i < sortFields.length; i++) {
                String actualFieldName = sortFields[i].replace("-", "");
                if (!propertyMap.containsKey(actualFieldName)) {
                    throw new BadRequestException("Invalid Sort Field: " + sortOption);
                }
                sortOption = sortOption.replace(actualFieldName,propertyMap.get(actualFieldName));
            }
        }else {
            String actualFieldName = sortOption.replace("-", "");
            if (!propertyMap.containsKey(actualFieldName)) {
                throw new BadRequestException("Invalid Sort Field: " + sortOption);
            }
            sortOption = sortOption.replace(actualFieldName,propertyMap.get(actualFieldName));
        }
        Map<String, Object> filterFields = new HashMap<>();
        if (!"".equals(enabled)) {
            filterFields.put("enabled", Boolean.parseBoolean(enabled));
        }
        if (!"".equals(regionName)) {
            filterFields.put("regionName", regionName);
        }
        if (!"".equals(countryCode)) {
            filterFields.put("countryCode", countryCode);
        }
        Page<Location> page = locationService.listByPage(
                pageNum - 1,
                pageSize,
                sortOption,
                filterFields
        );
        List<Location> locations = page.getContent();
        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(addPageMetaDataAndLinks(listEntity2ListDTO(locations), page, sortOption, enabled, regionName, countryCode));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Object> getLocation(@PathVariable("code") String code) throws GeolocationException, IOException {
        Location location = locationService.getLocation(code);
        return ResponseEntity.ok(addLinksToItem(entity2DTO(location)));
    }

    @PutMapping
    public ResponseEntity<Object> updateLocation(@Valid @RequestBody LocationDTO locationDTO) throws GeolocationException, IOException {
        Location updateLocation = locationService.update(dtoToEntity(locationDTO));
        return ResponseEntity.ok(addLinksToItem(entity2DTO(updateLocation)));
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

    private CollectionModel<LocationDTO> addPageMetaDataAndLinks(
            List<LocationDTO> listDto,
            Page<Location> pageInfo,
            String sortField,
            String enabled,
            String regionName,
            String countryCode
    ) throws BadRequestException {
        String actualEnabled = "".equals(enabled) ? null : enabled;
        String actualRegionName = "".equals(regionName) ? null : regionName;
        String actualCountryCode = "".equals(countryCode) ? null : countryCode;
        listDto.forEach(dto ->
                {
                    try {
                        dto.add(
                                linkTo(
                                        methodOn(LocationApiController.class)
                                                .getLocation(dto.getCode())
                                ).withSelfRel());
                    } catch (GeolocationException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        int pageSize = pageInfo.getSize();
        int pageNum = pageInfo.getNumber() + 1;
        long totalElements = pageInfo.getTotalElements();
        int totalPages = pageInfo.getTotalPages();

        PageMetadata pageMetadata = new PageMetadata(pageSize, pageNum, totalElements);
        CollectionModel<LocationDTO> collectionModels = PagedModel.of(listDto, pageMetadata);
        collectionModels.add(
                linkTo(
                        methodOn(LocationApiController.class)
                                .listLocation(pageNum, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode)
                ).withSelfRel()
        );
        if (pageNum > 1) {
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode)
                    ).withRel(IanaLinkRelations.FIRST)
            );
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(pageNum - 1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode)
                    ).withRel(IanaLinkRelations.PREV)
            );
        }
        if (pageNum < totalPages) {
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(pageNum + 1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode)
                    ).withRel(IanaLinkRelations.NEXT)
            );
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(totalPages, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode)
                    ).withRel(IanaLinkRelations.LAST)
            );
        }
        return collectionModels;
    }

    private LocationDTO addLinksToItem(
            LocationDTO dto
    ) throws GeolocationException, IOException {
        dto.add(
                linkTo(
                        methodOn(LocationApiController.class)
                                .getLocation(dto.getCode())
                ).withSelfRel());
        dto.add(
                linkTo(
                        methodOn(DailyWeatherApiController.class)
                                .listDailyForecastByLocationCode(dto.getCode())
                ).withRel("daily_forecast"));
        dto.add(
                linkTo(
                        methodOn(HourlyWeatherApiController.class)
                                .findHourlyWeatherForecastByLocationCode(null, dto.getCode())
                ).withRel("hourly_forecast"));
        dto.add(
                linkTo(
                        methodOn(RealtimeWeatherApiController.class)
                                .getRealtimeWeatherByLocationCode(dto.getCode())
                ).withRel("realtime_weather"));
        dto.add(
                linkTo(
                        methodOn(FullWeatherApiController.class)
                                .getFullWeatherByLocationCode(dto.getCode())
                ).withRel("full_forecast"));
        return dto;
    }
}
