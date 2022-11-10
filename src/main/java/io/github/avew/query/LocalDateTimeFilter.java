package io.github.avew.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Developed by Asep Rojali on 12/19/18 10:49 AM
 * Last modified 8/20/18 4:29 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Filter class for {@link LocalDate} type attributes.
 *
 * @see RangeFilter
 */
public class LocalDateTimeFilter extends RangeFilter<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    @Override
    public LocalDateTimeFilter setEquals(LocalDateTime equals) {
        super.setEquals(equals);
        return this;
    }

    @Override
    public LocalDateTimeFilter setGreaterThan(LocalDateTime equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @Override
    public LocalDateTimeFilter setGreaterOrEqualThan(LocalDateTime equals) {
        super.setGreaterOrEqualThan(equals);
        return this;
    }

    @Override
    public LocalDateTimeFilter setLessThan(LocalDateTime equals) {
        super.setLessThan(equals);
        return this;
    }

    @Override
    public LocalDateTimeFilter setLessOrEqualThan(LocalDateTime equals) {
        super.setLessOrEqualThan(equals);
        return this;
    }

    @Override
    public LocalDateTimeFilter setIn(List<LocalDateTime> in) {
        super.setIn(in);
        return this;
    }

}
