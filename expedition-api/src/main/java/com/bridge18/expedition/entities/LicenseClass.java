package com.bridge18.expedition.entities;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by Viktor on 01.03.2017.
 */
public enum LicenseClass {
    NONE,
    CLASS_A,
    CLASS_B;

    @JsonValue
    public Integer toValue() {
        return  this.ordinal();
    }
}
