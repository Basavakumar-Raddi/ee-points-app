package com.eesti.energia.point.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.eesti.energia.point.util.LocationEnum;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "point")
@Getter
@Setter
public class Point extends AbstractAuditable{

    @Column(name = "MEASUREMENT_DAY")
    private Long measurementDay;

    @Column(name = "MEASUREMENT_LOCATION")
    @Enumerated(EnumType.STRING)
    private LocationEnum location;

    @Column(name = "MEASUREMENT_VALUE")
    private Double value;

}
