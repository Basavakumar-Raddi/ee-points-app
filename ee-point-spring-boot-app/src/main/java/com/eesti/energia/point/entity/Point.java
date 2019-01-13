package com.eesti.energia.point.entity;

import com.eesti.energia.point.util.LocationEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
