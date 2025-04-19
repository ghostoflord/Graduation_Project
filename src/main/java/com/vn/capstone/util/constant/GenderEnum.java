package com.vn.capstone.util.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GenderEnum {
    FEMALE, MALE, OTHER;

    @JsonCreator
    public static GenderEnum fromString(String value) {
        if (value == null)
            return null;
        return GenderEnum.valueOf(value.toUpperCase());
    }
}