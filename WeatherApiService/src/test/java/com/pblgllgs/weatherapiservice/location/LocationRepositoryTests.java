package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class LocationRepositoryTests {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testAddSuccess(){
        Location location = new Location();
        location.setCode("NYC_USA");
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");
        location.setEnabled(true);
        Location locationSaved = locationRepository.save(location);
        assertThat(locationSaved).isNotNull();
        assertThat(locationSaved.getCode())
                .isEqualTo("NYC_USA");
    }

    @Test
    void testListSuccess() {
        List<Location> locations = locationRepository.findUntrashed();
        assertThat(locations).isNotEmpty();
        locations.forEach(System.out::println);
    }

    @Test
    void testGetNotFound404() {
        String code = "ASD";
        Location op = locationRepository.findByCode(code);
        assertThat(op).isNull();
    }

    @Test
    void testGetSuccess200() {
        String code = "NYC_USA";
        Location lo = locationRepository.findByCode(code);
        assertThat(lo).isNotNull();
    }

    @Test
    void testTrashed200() {
        String code = "NYC_USA";
        locationRepository.trashByCode(code);
        Location location = locationRepository.findByCode(code);
        assertThat(location).isNull();
    }
}