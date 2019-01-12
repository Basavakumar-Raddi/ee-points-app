package com.eesti.energia.point.dto;

import com.eesti.energia.point.util.LocationEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointDTO {

    private String id;
    private String measurementDay;
    private LocationEnum location;
    private Double value;
}
