package com.campers_helper.bot.commands;

import lombok.Getter;

@Getter
public enum UnitType {
    AMPERE("unit.ampere"),
    VOLT("unit.volt"),
    WATT("unit.watt");

    private final String messageKey;

    UnitType(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getCallbackData() {
        return name().toLowerCase();
    }

    public static UnitType fromCallback(String callbackData) {
        return valueOf(callbackData.toUpperCase());
    }
}
