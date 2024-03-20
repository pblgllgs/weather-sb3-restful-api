package com.pblgllgs.weatherapiservice.location;
/*
 *
 * @author pblgl
 * Created on 20-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.common.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FilterableLocationRepositoryImpl implements FilterableLocationRepository {

    private final EntityManager entityManager;

    public FilterableLocationRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Location> listWithFilter(Pageable pageable, Map<String, Object> filterFields) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Location> query = criteriaBuilder.createQuery(Location.class);

        Root<Location> root = query.from(Location.class);

        if (!filterFields.isEmpty()){
            Predicate[]predicates = new Predicate[filterFields.size()];
            Iterator<String> iterator = filterFields.keySet().iterator();

            int i = 0;
            while(iterator.hasNext()){
                String fieldName = iterator.next();
                Object filterValue = filterFields.get(fieldName);
                log.info(fieldName + " => " + filterValue);
                predicates[i++] = criteriaBuilder.equal(root.get(fieldName),filterValue);
            }
            query.where(predicates);

        }

        List<Order> listOrder = new ArrayList<>();

        pageable.getSort().stream().forEach( order -> {
            log.info("Order field: " +order.getProperty());
            listOrder.add(criteriaBuilder.asc(root.get(order.getProperty())));
        });

        query.orderBy(listOrder);

        TypedQuery<Location> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Location> resultList = typedQuery.getResultList();

        int totalRows = 0;
        return new PageImpl<>(resultList, pageable, totalRows);
    }
}
