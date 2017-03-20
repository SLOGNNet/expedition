package com.bridge18.expedition.entities.equipment;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OperatingMode {
    INTER_STATE,
    INTRA_STATE;

    @JsonValue
    public Integer toValue() {
        return  this.ordinal();
    }
}