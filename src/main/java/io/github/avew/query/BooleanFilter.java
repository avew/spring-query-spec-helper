package io.github.avew.query;

/*
 * Developed by Asep Rojali on 12/19/18 10:49 AM
 * Last modified 8/20/18 4:29 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Class for filtering attributes with {@link Boolean} type. It can be added to a criteria class as a member, to support
 * the following query parameters:
 * <pre>
 *      fieldName.equals=true
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.writer=true,false
 * </pre>
 */
public class BooleanFilter extends Filter<Boolean> {

    private static final long serialVersionUID = 1L;

    public BooleanFilter() {
    }

}
