package com.pblgllgs.weatherapiservice.location;
/*
 *
 * @author pblgl
 * Created on 19-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.common.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocationCriteriaQueryTests {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testCriteriaQuery() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> query = builder.createQuery(Location.class);
        Root<Location> root = query.from(Location.class);

        Predicate predicate = builder.equal(root.get("countryCode"), "US");
        query.where(predicate);

        query.orderBy(builder.asc(root.get("cityName")));

        TypedQuery<Location> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(4);


        List<Location> resultList = typedQuery.getResultList();
        assertThat(resultList).isNotEmpty();
        resultList.forEach(System.out::println);
    }

    @Test
    void testJPQL() {
        String jpql = "FROM Location WHERE countryCode = 'US' ORDER BY countryName";
        TypedQuery<Location> query = entityManager.createQuery(jpql, Location.class);
        List<Location> resultList = query.getResultList();
        assertThat(resultList).isNotEmpty();
        resultList.forEach(System.out::println);
    }
}
