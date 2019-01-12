package com.point.console;

public enum LocationEnum {
    EE("EE"),
    FI("FI"),
    LV("LV");

    private final String location;

    LocationEnum(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public static LocationEnum getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
