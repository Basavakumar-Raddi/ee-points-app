package com.eesti.energia.point.dao;

import com.eesti.energia.point.entity.Point;
import com.eesti.energia.point.util.LocationEnum;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, String> {

    public Point findByMeasurementDayAndLocation(Long measurementDay, LocationEnum location);

}
