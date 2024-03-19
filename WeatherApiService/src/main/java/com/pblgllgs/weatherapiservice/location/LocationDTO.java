package com.pblgllgs.weatherapiservice.location;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(collectionRelation = "locations")
public class LocationDTO extends CollectionModel<LocationDTO> {
    @NotNull(message = "Location code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must ve 3-12 characters")
    private String code;
    @NotNull(message = "City name cannot be null")
    @Length(min = 3, max = 128, message = "City name must ve 3-128 characters")
    private String cityName;
    @Length(min = 3, max = 128, message = "Region name must ve 3-128 characters")
    private String regionName;
    @NotNull(message = "Country name cannot be null")
    @Length(min = 3, max = 64, message = "Country name must ve 3-64 characters")
    private String countryName;
    @NotNull(message = "Country code cannot be null")
    @Length(min = 2, max = 2, message = "Country code must be 2 characters")
    private String countryCode;
    private boolean enabled;

}
