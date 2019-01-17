package com.eesti.energia.point.dto;

import com.eesti.energia.point.util.LocationEnum;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDTO {

    private String id;
    private String measurementDay;
    private LocationEnum location;
    private Double value;
}
