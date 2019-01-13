package com.eesti.energia.point.entity;

import com.eesti.energia.point.util.LocationEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

@Entity
@Table(name = "point")
@Getter
@Setter
@NamedQuery(name="Point.findByMeasurementDayAndLocation",
        query="SELECT p FROM Point p WHERE p.measurementDay = :measurementDay AND p.location = :location",
        lockMode = PESSIMISTIC_WRITE,
        hints={@QueryHint(name="javax.persistence.query.timeout", value="5000")})
public class Point extends AbstractAuditable{

    @Column(name = "MEASUREMENT_DAY")
    private Long measurementDay;

    @Column(name = "MEASUREMENT_LOCATION")
    @Enumerated(EnumType.STRING)
    private LocationEnum location;

    @Column(name = "MEASUREMENT_VALUE")
    private Double value;

}
