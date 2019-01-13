package com.eesti.energia.point.dao;

import com.eesti.energia.point.entity.Point;
import com.eesti.energia.point.util.LocationEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, String> {

    public Point findByMeasurementDayAndLocation(@Param("measurementDay") Long measurementDay,
                                                 @Param("location") LocationEnum location);


}
