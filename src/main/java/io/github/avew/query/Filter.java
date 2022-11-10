package io.github.avew.query;

import java.io.Serializable;
import java.util.List;


/*
 * Developed by Asep Rojali on 12/19/18 10:49 AM
 * Last modified 8/20/18 4:29 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Base class for the various attribute filters. It can be added to a criteria class as a member, to support the
 * following query parameters:
 * <pre>
 *      fieldName.equals='something'
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.writer='something','other'
 * </pre>
 */
public class Filter<FIELD_TYPE> implements Serializable {

    private static final long serialVersionUID = 1L;
    private FIELD_TYPE equals;
    private Boolean specified;
    private List<FIELD_TYPE> in;
    private FIELD_TYPE contains;
    private FIELD_TYPE notEquals;

    public FIELD_TYPE getContains() {
        return contains;
    }

    public Filter<FIELD_TYPE> setContains(FIELD_TYPE contains) {
        this.contains = contains;
        return this;
    }

    public FIELD_TYPE getNotEquals() {
        return notEquals;
    }

    public Filter<FIELD_TYPE> setNotEquals(FIELD_TYPE notEquals) {
        this.notEquals = notEquals;
        return this;
    }

    public FIELD_TYPE getEquals() {
        return equals;
    }

    public Filter<FIELD_TYPE> setEquals(FIELD_TYPE equals) {
        this.equals = equals;
        return this;
    }


    public Boolean getSpecified() {
        return specified;
    }

    public Filter<FIELD_TYPE> setSpecified(Boolean specified) {
        this.specified = specified;
        return this;
    }

    public List<FIELD_TYPE> getIn() {
        return in;
    }

    public Filter<FIELD_TYPE> setIn(List<FIELD_TYPE> in) {
        this.in = in;
        return this;
    }

    @Override
    public String toString() {
        return "Filter ["
                + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
                + (getIn() != null ? "writer=" + getIn() : "")
                + (getSpecified() != null ? "specified=" + getSpecified() : "")
                + "]";
    }


    //api/v1/bp-21-tf (CRUD)
    //api/v1/bp-21-tf/123123123/spt

    //api/v1/spt/54321/company
    //api/v1/spt/123123

    //api/v1/user GET ALL
    //api/v1/user/{id} GET BY ID
    //api/v1/user/{id}/activation ACTIVATION USER BY ID
    //api/v1/company/{companyId}/user

    //api/v1/

    //api/v1/npwp/company/123123

    //api/v1/setting/by-npwp?namaWp.equals="123"
    //api/v1/setting/byNpwp
    //api/v1/setting/by/npwp


    //api/v1/company/{companyId}/subscription/{subscriptionId}

}
