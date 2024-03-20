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
        CriteriaQuery<Location> entityQuery = criteriaBuilder.createQuery(Location.class);

        Root<Location> entityRoot = entityQuery.from(Location.class);

        Predicate[] predicates = createPredicates(filterFields, criteriaBuilder, entityRoot);

        if (predicates.length > 0) entityQuery.where(predicates);

        List<Order> listOrder = new ArrayList<>();

        pageable.getSort().stream().forEach( order -> {
            log.info("Order field: " +order.getProperty());
            listOrder.add(criteriaBuilder.asc(entityRoot.get(order.getProperty())));
        });

        entityQuery.orderBy(listOrder);

        TypedQuery<Location> typedQuery = entityManager.createQuery(entityQuery);

        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Location> resultList = typedQuery.getResultList();

        long totalRows = getTotalRows(filterFields);

        return new PageImpl<>(resultList, pageable, totalRows);
    }

    private Predicate[] createPredicates(
            Map<String, Object> filterFields,
            CriteriaBuilder criteriaBuilder,
            Root<Location> root) {
        Predicate[]predicates = new Predicate[filterFields.size() + 1];
        if (!filterFields.isEmpty()){
            Iterator<String> iterator = filterFields.keySet().iterator();
            int i = 0;
            while(iterator.hasNext()){
                String fieldName = iterator.next();
                Object filterValue = filterFields.get(fieldName);
                log.info(fieldName + " => " + filterValue);
                predicates[i++] = criteriaBuilder.equal(root.get(fieldName),filterValue);
            }
        }
        predicates[predicates.length -1 ] = criteriaBuilder.equal(root.get("trashed"),false);
        return predicates;
    }

    private long getTotalRows(Map<String, Object> filterFields){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

        Root<Location> countRoot = countQuery.from(Location.class);

        countQuery.select(builder.count(countRoot));

        Predicate[] predicates = createPredicates(filterFields, builder, countRoot);
        
        if (predicates.length > 0) countQuery.where(predicates);

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
