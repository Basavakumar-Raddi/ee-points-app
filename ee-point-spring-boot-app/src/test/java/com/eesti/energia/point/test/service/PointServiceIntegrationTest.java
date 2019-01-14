package com.eesti.energia.point.test.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.eesti.energia.point.dao.PointRepository;
import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.service.PointService;
import com.eesti.energia.point.util.LocationEnum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;
    @Autowired
    private PointRepository pointRepository;


    @Test
    public void testAddSequential() {
        PointDTO pointDto =  new PointDTO();
        pointDto.setLocation(LocationEnum.LV);
        pointDto.setMeasurementDay("2088-02-29");
        pointDto.setValue(15.15);
        PointDTO pointDTOResp = null;

        try {
            pointDTOResp = pointService.addPoint(pointDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService es = Executors.newFixedThreadPool(3);
        try {
            es.execute(() -> {
                PointDTO firstThreadResp = updatePointThrd1(pointDto);
                Assert.assertEquals(firstThreadResp.getValue(),Double.valueOf("30.30"));
            });
            es.execute(() -> {
                //simulating other user by using different thread
                PointDTO secondThreadResp = updatePointThrd2(pointDto);
                Assert.assertEquals(secondThreadResp.getValue(),Double.valueOf("45.45"));
            });
            es.shutdown();
            es.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(pointDTOResp!=null){
                pointRepository.deleteById(pointDTOResp.getId());
            }
        }

    }

    @Test
    public void testAddConcurrent() {
        List values = new ArrayList();
        values.add(20.22);
        values.add(30.33);
        PointDTO pointDto =  new PointDTO();
        pointDto.setLocation(LocationEnum.EE);
        pointDto.setMeasurementDay("2088-02-29");
        pointDto.setValue(10.11);

        PointDTO pointDTOResp = null;
        try {
            pointDTOResp = pointService.addPoint(pointDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService es = Executors.newFixedThreadPool(3);
        try {
            es.execute(() -> {
                PointDTO firstThreadResp = updatePointThrd1(pointDto);
                Assert.assertTrue(values.contains(firstThreadResp.getValue()));
                values.remove(firstThreadResp.getValue());
            });
            es.execute(() -> {
                //simulating other request by using different thread
                PointDTO secondThreadResp = updatePointThrd3(pointDto);
                Assert.assertTrue(values.contains(secondThreadResp.getValue()));
            });
            es.shutdown();
            es.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(pointDTOResp!=null){
                pointRepository.deleteById(pointDTOResp.getId());
            }
        }


    }

    private PointDTO updatePointThrd1(PointDTO pointDto) {
        PointDTO foundPoint = null;
        try {
            foundPoint = pointService.addPoint(pointDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return foundPoint;
    }

    private PointDTO updatePointThrd2(PointDTO pointDto) {
        PointDTO foundPoint = null;
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            foundPoint = pointService.addPoint(pointDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return foundPoint;
    }
    private PointDTO updatePointThrd3(PointDTO pointDto) {
        PointDTO foundPoint = null;
        try {
            foundPoint = pointService.addPoint(pointDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return foundPoint;
    }

}
