package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapicommon.common.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

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
}