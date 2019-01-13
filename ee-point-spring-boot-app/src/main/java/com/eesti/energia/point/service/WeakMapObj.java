package com.eesti.energia.point.service;

import com.eesti.energia.point.entity.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class WeakMapObj {
    private static final long serialVersionUID = 1L;

    private String measurementDay;
    private String location;
    private Point point;

    public WeakMapObj(){}

    public WeakMapObj(Long measurementDay, String location, Point point) {
        this.measurementDay = measurementDay.toString();
        this.location = location;
        this.point = point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeakMapObj that = (WeakMapObj) o;
        return measurementDay.equals(that.measurementDay) &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measurementDay, location);
    }
}
