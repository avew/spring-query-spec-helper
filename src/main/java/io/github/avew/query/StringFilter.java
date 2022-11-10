package io.github.avew.query;

/*
 * Developed by Asep Rojali on 12/19/18 10:49 AM
 * Last modified 8/20/18 4:29 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Class for filtering attributes with {@link String} type.
 * It can be added to a criteria class as a member, to support the following query parameters:
 * <code>
 *      fieldName.equals='something'
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.writer='something','other'
 *      fieldName.contains='thing'
 * </code>
 */
public class StringFilter extends Filter<String> {

    private static final long serialVersionUID = 1L;

    private String contains;

    private String notEquals;

    public String getContains() {
        return contains;
    }

    public StringFilter setContains(String contains) {
        this.contains = contains;
        return this;
    }

    public String getNotEquals() {
        return notEquals;
    }

    public StringFilter setNotEquals(String notEquals) {
        this.notEquals = notEquals;
        return this;
    }

    @Override
    public String toString() {
        return "StringFilter ["
            + (getContains() != null ? "contains=" + getContains() + ", " : "")
            + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
            + (getNotEquals() != null ? "notEquals=" + getNotEquals() + ", " : "")
            + (getSpecified() != null ? "specified=" + getSpecified() : "")
            + "]";
    }

}
