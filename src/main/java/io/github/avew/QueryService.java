package io.github.avew;

import io.github.avew.query.BigDecimalFilter;
import io.github.avew.query.Filter;
import io.github.avew.query.RangeFilter;
import io.github.avew.query.StringFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;


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
@Transactional(readOnly = true)
public abstract class QueryService<ENTITY> {

    /**
     * Helper function to return a specification for filtering on a single field, where equality, and null/non-null
     * conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @param <X>    The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X> Specification<ENTITY> buildSpecification(Filter<X> filter, SingularAttribute<? super ENTITY, X>
            field) {
        if (filter.getEquals() != null) {
            return equalsSpecification(field, filter.getEquals());
        }
        else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        }
        else if (filter.getSpecified() != null) {
            return byFieldSpecified(field, filter.getSpecified());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a {@link String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected Specification<ENTITY> buildStringSpecification(StringFilter filter, SingularAttribute<? super ENTITY,
            String> field) {
        if (filter.getEquals() != null) {
            return equalsSpecification(field, filter.getEquals());
        }
        else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(field, filter.getNotEquals());
        }
        else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        }
        else if (filter.getContains() != null) {
            return likeUpperSpecification(field, filter.getContains());
        }
        else if (filter.getSpecified() != null) {
            return byFieldSpecified(field, filter.getSpecified());
        }
        return null;
    }


    /**
     * Helper function to return a specification for filtering on a {@link String} field, where equality, containment,
     * and null/non-null conditions are supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @return a Specification
     */
    protected Specification<ENTITY> buildBigDecimalSpecification(BigDecimalFilter filter, SingularAttribute<? super ENTITY, BigDecimal> field) {
        if (filter.getEquals() != null) {
            return equalsSpecification(field, filter.getEquals());
        }
        else if (filter.getNotEquals() != null) {
            return notEqualsSpecification(field, filter.getNotEquals());
        }
        else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        }
        else if (filter.getSpecified() != null) {
            return byFieldSpecified(field, filter.getSpecified());
        }
        else if (filter.getGreaterThan() != null) {
            return greaterThan(field, filter.getGreaterThan());
        }
        else if (filter.getGreaterOrEqualThan() != null) {
            return greaterThanOrEqualTo(field, filter.getGreaterOrEqualThan());
        }
        else if (filter.getLessThan() != null) {
            return lessThan(field, filter.getLessThan());
        }
        else if (filter.getLessOrEqualThan() != null) {
            return lessThanOrEqualTo(field, filter.getLessOrEqualThan());
        }
        return null;
    }

    /**
     * Helper function to return a specification for filtering on a single {@link Comparable}, where equality, less
     * than, greater than and less-than-or-equal-to and greater-than-or-equal-to and null/non-null conditions are
     * supported.
     *
     * @param filter the individual attribute filter coming from the frontend.
     * @param field  the JPA static metamodel representing the field.
     * @param <X>    The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <X extends Comparable<? super X>> Specification<ENTITY> buildRangeSpecification(RangeFilter<X> filter,
                                                                                              SingularAttribute<? super ENTITY, X> field) {
        if (filter.getEquals() != null) {
            return equalsSpecification(field, filter.getEquals());
        }
        else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        }

        Specification<ENTITY> result = Specification.where(null);
        if (filter.getSpecified() != null) {
            result = result.and(byFieldSpecified(field, filter.getSpecified()));
        }
        if (filter.getGreaterThan() != null) {
            result = result.and(greaterThan(field, filter.getGreaterThan()));
        }
        if (filter.getGreaterOrEqualThan() != null) {
            result = result.and(greaterThanOrEqualTo(field, filter.getGreaterOrEqualThan()));
        }
        if (filter.getLessThan() != null) {
            result = result.and(lessThan(field, filter.getLessThan()));
        }
        if (filter.getLessOrEqualThan() != null) {
            result = result.and(lessThanOrEqualTo(field, filter.getLessOrEqualThan()));
        }
        return result;
    }

    /**
     * Helper function to return a specification for filtering on one-to-one or many-to-one reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByProjectId = buildReferringEntitySpecification(criteria.getProjectId(),
     * Employee_.project, Project_.id);
     *   Specification&lt;Employee&gt; specByProjectName = buildReferringEntitySpecification(criteria.getProjectName(),
     * Employee_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if nullness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring entity.
     * @param valueField the attribute of the static metamodel of the referred entity, where the equality should be
     *                   checked.
     * @param <OTHER>    The type of the referenced entity.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <OTHER, X> Specification<ENTITY> buildReferringEntitySpecification(Filter<X> filter,
                                                                                 SingularAttribute<? super ENTITY, OTHER> reference,
                                                                                 SingularAttribute<OTHER, X> valueField) {
        Specification<ENTITY> spec = Specification.where(null);
        if (filter.getEquals() != null) {
            spec = spec.and(equalsSpecification(reference, valueField, filter.getEquals()));
        }
        if (filter.getIn() != null) {
            spec = spec.and(valueIn(reference, valueField, filter.getIn()));
        }
        if (filter.getContains() != null) {
            spec = spec.and(likeUpperSpecification(reference, valueField, filter.getContains()));
        }
        if (filter.getSpecified() != null) {
            spec = spec.and(byFieldSpecified(reference, filter.getSpecified()));
        }
        if (filter instanceof RangeFilter) {
            RangeFilter<Comparable<? super Comparable<?>>> rangeFilter =
                    (RangeFilter<Comparable<? super Comparable<?>>>) filter;

            SingularAttribute<OTHER, Comparable<? super Comparable<?>>> rangeValue =
                    (SingularAttribute<OTHER, Comparable<? super Comparable<?>>>) valueField;

            spec = spec.and(buildReferringRangeSpecification(rangeFilter, reference, rangeValue));
        }

        return spec;
    }

    private <X extends Comparable<? super X>, TARGET> Specification<ENTITY> buildReferringRangeSpecification(RangeFilter<X> filter,
                                                                                                             SingularAttribute<? super ENTITY, TARGET> reference,
                                                                                                             SingularAttribute<TARGET, X> value) {
        Specification<ENTITY> spec = Specification.where(null);

        if (filter.getGreaterThan() != null)
            spec = spec.and(this.greaterThanSpecification(reference, value, filter.getGreaterThan(), false));
        if (filter.getLessThan() != null)
            spec = spec.and(this.lessThanSpecification(reference, value, filter.getLessThan(), false));

        if (filter.getGreaterOrEqualThan() != null)
            spec = spec.and(this.greaterThanSpecification(reference, value, filter.getGreaterOrEqualThan(), true));
        if (filter.getLessOrEqualThan() != null)
            spec = spec.and(this.lessThanSpecification(reference, value, filter.getLessOrEqualThan(), true));

        return spec;
    }

    /**
     * Helper function to return a specification for filtering on one-to-many or many-to-many reference. Usage:
     * <pre>
     *   Specification&lt;Employee&gt; specByEmployeeId = buildReferringEntitySpecification(criteria.getEmployeId(),
     * Project_.employees, Employee_.id);
     *   Specification&lt;Employee&gt; specByEmployeeName = buildReferringEntitySpecification(criteria.getEmployeName(),
     * Project_.project, Project_.name);
     * </pre>
     *
     * @param filter     the filter object which contains a value, which needs to match or a flag if emptiness is
     *                   checked.
     * @param reference  the attribute of the static metamodel for the referring entity.
     * @param valueField the attribute of the static metamodel of the referred entity, where the equality should be
     *                   checked.
     * @param <OTHER>    The type of the referenced entity.
     * @param <X>        The type of the attribute which is filtered.
     * @return a Specification
     */
    protected <OTHER, X> Specification<ENTITY> buildReferringEntitySpecification(Filter<X> filter,
                                                                                 SetAttribute<ENTITY, OTHER> reference,
                                                                                 SingularAttribute<OTHER, X> valueField) {
        if (filter.getEquals() != null) {
            return equalsSetSpecification(reference, valueField, filter.getEquals());
        }
        else if (filter.getSpecified() != null) {
            return byFieldSpecified(reference, filter.getSpecified());
        }
        return null;
    }

    protected <X> Specification<ENTITY> equalsSpecification(SingularAttribute<? super ENTITY, X> field, final X value) {
        return (root, query, builder) -> builder.equal(root.get(field), value);
    }


    protected <X> Specification<ENTITY> notEqualsSpecification(SingularAttribute<? super ENTITY, X> field, final X value) {
        return (root, query, builder) -> builder.notEqual(root.get(field), value);
    }

    protected <OTHER, X> Specification<ENTITY> equalsSpecification(SingularAttribute<? super ENTITY, OTHER> reference,
                                                                   SingularAttribute<OTHER, X> idField,
                                                                   X value) {
        return (root, query, builder) -> builder.equal(root.get(reference).get(idField), value);
    }

    protected <X> Specification<ENTITY> equalsSpecification(Iterable<SingularAttribute<Object, Object>> refs, X value) {
        return (r, q, b) -> {
            Path<Object> path = chainReferringPaths(r, refs);
            return b.equal(path, value);
        };
    }

    protected <OTHER, X> Specification<ENTITY> likeUpperSpecification(SingularAttribute<? super ENTITY, OTHER> reference,
                                                                      SingularAttribute<OTHER, X> idField,
                                                                      X value) {
        return (root, query, builder) -> builder.like(builder.upper(root.get(reference).get(idField.getName())), wrapLikeQuery(value.toString()));
    }

    protected <OTHER, X> Specification<ENTITY> equalsSetSpecification(SetAttribute<? super ENTITY, OTHER> reference,
                                                                      SingularAttribute<OTHER, X> idField,
                                                                      X value) {
        return (root, query, builder) -> builder.equal(root.join(reference).get(idField), value);
    }

    protected <OTHER, X extends Comparable<? super X>> Specification<ENTITY> greaterThanSpecification(SingularAttribute<? super ENTITY, OTHER> reference,
                                                                                                      SingularAttribute<OTHER, X> idField,
                                                                                                      X value,
                                                                                                      boolean equal) {
        return (root, query, builder) -> {
            if (equal) return builder.greaterThanOrEqualTo(root.get(reference).get(idField), value);
            return builder.greaterThan(root.get(reference).get(idField), value);
        };
    }

    protected <OTHER, X extends Comparable<? super X>> Specification<ENTITY> lessThanSpecification(SingularAttribute<? super ENTITY, OTHER> reference,
                                                                                                   SingularAttribute<OTHER, X> idField,
                                                                                                   X value,
                                                                                                   boolean equal) {
        return (root, query, builder) -> {
            if (equal) return builder.lessThanOrEqualTo(root.get(reference).get(idField), value);
            return builder.lessThan(root.get(reference).get(idField), value);
        };
    }


    protected Specification<ENTITY> likeUpperSpecification(SingularAttribute<? super ENTITY, String> field, final
    String value) {

        return (root, query, builder) -> builder.like(builder.upper(root.get(field)), wrapLikeQuery(value));
    }


    protected <X> Specification<ENTITY> byFieldSpecified(SingularAttribute<? super ENTITY, X> field, final boolean
            specified) {
        return specified ?
               (root, query, builder) -> builder.isNotNull(root.get(field)) :
               (root, query, builder) ->
                       builder.isNull(root.get(field));
    }

    protected <X> Specification<ENTITY> byFieldSpecified(SetAttribute<ENTITY, X> field, final boolean specified) {
        return specified ?
               (root, query, builder) -> builder.isNotEmpty(root.get(field)) :
               (root, query, builder) ->
                       builder.isEmpty(root.get(field));
    }

    protected <X> Specification<ENTITY> valueIn(SingularAttribute<? super ENTITY, X> field, final Collection<X>
            values) {
        return (root, query, builder) -> {
            In<X> in = builder.in(root.get(field));
            for (X value : values) {
                in = in.value(value);
            }
            return in;
        };
    }

    protected <OTHER, X> Specification<ENTITY> valueIn(SingularAttribute<? super ENTITY, OTHER> reference,
                                                       SingularAttribute<OTHER, X> valueField, final Collection<X> values) {
        return (root, query, builder) -> {
            In<X> in = builder.in(root.get(reference).get(valueField));
            for (X value : values) {
                in = in.value(value);
            }
            return in;
        };
    }

    protected <X> Specification<ENTITY> valueIn(Iterable<SingularAttribute<Object, Object>> refs,
                                                       final Collection<X> values) {
        return (root, query, builder) -> {
            Path<X> path = castTo(chainReferringPaths(root, refs));
            In<X> in = builder.in(path);
            for (X value : values) {
                in = in.value(value);
            }
            return in;
        };
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> greaterThanOrEqualTo(SingularAttribute<? super
            ENTITY, X> field, final X value) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(field), value);
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> greaterThan(SingularAttribute<? super ENTITY,
            X> field, final X value) {
        return (root, query, builder) -> builder.greaterThan(root.get(field), value);
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> lessThanOrEqualTo(SingularAttribute<? super
            ENTITY, X> field, final X value) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get(field), value);
    }

    protected <X extends Comparable<? super X>> Specification<ENTITY> lessThan(SingularAttribute<? super ENTITY, X>
                                                                                       field, final X value) {
        return (root, query, builder) -> builder.lessThan(root.get(field), value);
    }

    protected String wrapLikeQuery(String txt) {
        return "%" + txt.toUpperCase() + '%';
    }

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
