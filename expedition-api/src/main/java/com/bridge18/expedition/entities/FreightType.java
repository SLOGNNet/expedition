package com.bridge18.expedition.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FreightType {
    DRY,
    REEFER;

    @JsonValue
    public Integer toValue() {
        return  this.ordinal();
    }
}