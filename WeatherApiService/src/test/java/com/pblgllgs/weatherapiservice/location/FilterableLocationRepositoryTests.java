package com.pblgllgs.weatherapiservice.location;

import com.pblgllgs.weatherapiservice.common.Location;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/*
 *
 * @author pblgl
 * Created on 20-03-2024
 *
 */
@DataJpaTest
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilterableLocationRepositoryTests {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void testListWithDefaults() {
        int pageSize = 5;
        int pageNum = 1;

        String sortField = "code";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Location> page = locationRepository.listWithFilter(pageable, Collections.emptyMap());

        List<Location> content = page.getContent();

        log.info("Total elements: " + page.getTotalElements());
        assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() +content.size());

        assertThat(content).size().isEqualTo(pageSize);
        assertThat(content).isSortedAccordingTo(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        content.forEach(System.out::println);
    }

    @Test
    void testListNoFilterAndSortedByCityName() {
        int pageSize = 5;
        int pageNum = 0;

        String sortField = "cityName";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Location> page = locationRepository.listWithFilter(pageable, Collections.emptyMap());

        List<Location> content = page.getContent();

        log.info("Total elements: " + page.getTotalElements());
        assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() +content.size());

        assertThat(content).size().isEqualTo(pageSize);
        assertThat(content).isSortedAccordingTo(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o1.getCityName().compareTo(o2.getCityName());
            }
        });

        content.forEach(System.out::println);
    }

    @Test
    void testListFilterRegionNameSortedByCityName() {
        int pageSize = 5;
        int pageNum = 0;

        String sortField = "cityName";
        String regionName = "California";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Map<String, Object> filterFields = Map.of("regionName", regionName);
        Page<Location> page = locationRepository.listWithFilter(pageable, filterFields);

        List<Location> content = page.getContent();

        log.info("Total elements: " + page.getTotalElements());
        assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() +content.size());

        assertThat(content).size().isEqualTo(pageSize);
        assertThat(content).isSortedAccordingTo(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o1.getCityName().compareTo(o2.getCityName());
            }
        });

        content.forEach(location -> assertThat(location.getRegionName()).isEqualTo(regionName));

        content.forEach(System.out::println);
    }

    @Test
    void testListFilterByCountryCodeSortedByCode() {
        int pageSize = 5;
        int pageNum = 0;

        String sortField = "code";
        String countryCode = "US";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Map<String, Object> filterFields = Map.of("countryCode", countryCode);
        Page<Location> page = locationRepository.listWithFilter(pageable, filterFields);

        List<Location> content = page.getContent();

        log.info("Total elements: " + page.getTotalElements());
        assertThat(page.getTotalElements()).isGreaterThan(pageable.getOffset() +content.size());

        assertThat(content).size().isEqualTo(pageSize);
        assertThat(content).isSortedAccordingTo(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        content.forEach(location -> assertThat(location.getCountryCode()).isEqualTo(countryCode));

        content.forEach(System.out::println);
    }

    @Test
    void testListFilterByCountryCodeAndEnableAndSortedByCityName() {
        int pageSize = 5;
        int pageNum = 0;

        String sortField = "cityName";
        boolean enabled = true;
        String countryCode = "US";

        Sort sort = Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Map<String, Object> filterFields = Map.of("countryCode", countryCode, "enabled", enabled);
        Page<Location> page = locationRepository.listWithFilter(pageable, filterFields);

        List<Location> content = page.getContent();

        assertThat(content).size().isEqualTo(pageSize);
        assertThat(content).isSortedAccordingTo(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o1.getCityName().compareTo(o2.getCityName());
            }
        });

        content.forEach(location -> {
            assertThat(location.getCountryCode()).isEqualTo(countryCode);
            assertThat(location.isEnabled()).isTrue();

        });

        content.forEach(System.out::println);
    }
}
