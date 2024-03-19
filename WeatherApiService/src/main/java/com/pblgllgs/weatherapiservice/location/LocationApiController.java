package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.BadRequestException;
import com.pblgllgs.weatherapiservice.common.Location;
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

import java.net.URI;
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
    public ResponseEntity<LocationDTO> addLocation(@Valid @RequestBody LocationDTO locationDTO) {
        Location addedLocation = locationService.add(dtoToEntity(locationDTO));
        URI uri = URI.create("/v1/locations/" + locationDTO.getCode());
        return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
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
            @RequestParam(value = "sort", required = false, defaultValue = "code") String sortField
    ) throws BadRequestException {
        if (!propertyMap.containsKey(sortField)) {
            throw new BadRequestException("Invalid Sort Field: " + sortField);
        }
        Page<Location> page = locationService.listByPage(pageNum - 1, pageSize, propertyMap.get(sortField));
        List<Location> locations = page.getContent();
        if (locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(addPageMetaDataAndLinks(listEntity2ListDTO(locations), page, sortField));
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

    private CollectionModel<LocationDTO> addPageMetaDataAndLinks(
            List<LocationDTO> listDto,
            Page<Location> pageInfo,
            String sortField
    ) throws BadRequestException {
        listDto.forEach(dto ->
                dto.add(
                        linkTo(
                                methodOn(LocationApiController.class)
                                        .getLocation(dto.getCode())
                        ).withSelfRel())
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
                                .listLocation(pageNum, pageSize, sortField)
                ).withSelfRel()
        );

        if (pageNum > 1) {
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(1, pageSize, sortField)
                    ).withRel(IanaLinkRelations.FIRST)
            );
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(pageNum - 1, pageSize, sortField)
                    ).withRel(IanaLinkRelations.PREV)
            );
        }

        if (pageNum < totalPages){
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(pageNum + 1, pageSize, sortField)
                    ).withRel(IanaLinkRelations.NEXT)
            );
            collectionModels.add(
                    linkTo(
                            methodOn(LocationApiController.class)
                                    .listLocation(totalPages, pageSize, sortField)
                    ).withRel(IanaLinkRelations.LAST)
            );
        }

        return collectionModels;
    }
}
