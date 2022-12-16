package io.github.avew.query;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.RangeFilter;
import tech.jhipster.service.filter.StringFilter;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;


/*
 * Developed by Asep Rojali on 12/19/18 10:49 AM
 * Last modified 12/11/18 12:41 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Base service for constructing and executing complex queries.
 *
 * @param <ENTITY> the type of the entity which is queried.
 */
public abstract class CustomQueryService<ENTITY> extends QueryService<ENTITY> {

    protected <X, F extends Filter<X>, ATTR extends SingularAttribute<? super ENTITY, X>> Specification<ENTITY> applyNonNull(
            Specification<ENTITY> spec,
            @Nullable F filter,
            ATTR attr,
            BiFunction<F, ATTR, Specification<ENTITY>> method) {
        if (filter != null) return spec.and(method.apply(filter, attr));
        return spec;
    }

    protected <X, OTHER, F extends Filter<X>> Specification<ENTITY> applyNonNull(
            Specification<ENTITY> spec,
            @Nullable F filter,
            SingularAttribute<? super ENTITY, OTHER> attr1,
            SingularAttribute<OTHER, X> attr2) {
        if (filter != null) return spec.and(buildReferringEntitySpecification(filter, attr1, attr2));
        return spec;
    }

    protected <X, OTHER, F extends Filter<X>> Specification<ENTITY> applyNonNull(
            Specification<ENTITY> spec,
            @Nullable F filter,
            SingularAttribute<? super ENTITY, OTHER> attr1,
            SingularAttribute<OTHER, X> attr2, JoinType joinType) {
        if (filter != null) return spec.and(buildReferringEntitySpecification(filter, attr1, attr2));
        return spec;
    }

    private Path<Object> chainReferringPaths(Root<ENTITY> root, Iterable<SingularAttribute<Object, Object>> refs) {
        Path<Object> paths = null;
        Iterator<SingularAttribute<Object, Object>> iterator = refs.iterator();

        SingularAttribute<Object, Object> ref = iterator.next();
        while (iterator.hasNext()) {
            if (paths == null) paths = root.get(ref);
            else paths = paths.get(ref);
        }
        return paths;
    }

    private <T> T castTo(Object o) {
        return (T) o;
    }
}
