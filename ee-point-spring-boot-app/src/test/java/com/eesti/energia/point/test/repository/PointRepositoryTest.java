package com.eesti.energia.point.test.repository;

import java.util.Date;

import com.eesti.energia.point.entity.Point;
import com.eesti.energia.point.dao.PointRepository;
import com.eesti.energia.point.util.LocationEnum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository pointRepository;

    @Test
    public void findByMeasurementDayAndLocationTest() {
        Point point = new Point();
        point.setValue(200.35);
        point.setLocation(LocationEnum.EE);
        point.setMeasurementDay(1545004800000L);
        point.setCreatedBy("testUser");
        point.setCreatedDate(new Date());
        entityManager.persist(point);
        entityManager.flush();

        Point foundPoint = pointRepository.findByMeasurementDayAndLocation(point.getMeasurementDay(),point.getLocation());

        Assert.assertEquals(foundPoint.getValue(),point.getValue());
    }

    @Test
    public void FailfindByMeasurementDayAndLocationTest() {
        Point point = new Point();
        point.setValue(200.35);
        point.setLocation(LocationEnum.EE);
        point.setMeasurementDay(1545004800000L);
        point.setCreatedBy("testUser");
        point.setCreatedDate(new Date());
        entityManager.persist(point);
        entityManager.flush();

        Point notFoundPoint = pointRepository.findByMeasurementDayAndLocation(12345678L,LocationEnum.FI);

        Assert.assertNull(notFoundPoint);
    }
}
