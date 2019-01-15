package com.eesti.energia.point.test.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.validation.constraints.AssertTrue;

import com.eesti.energia.point.PointApplication;
import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.service.PointService;
import com.eesti.energia.point.util.LocationEnum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PointApplication.class)
@ComponentScan("com.eesti.energia.point")
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Test
    public void testAddSequential() throws InterruptedException, ExecutionException {
        PointDTO pointDto =  PointDTO.builder()
                .location(LocationEnum.LV)
                .measurementDay("2088-02-29")
                .value(15.15).build();
        addPoint(pointDto);
        List<Double> expectedValues = Arrays.asList(30.30, 45.45);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService es = new ExecutorCompletionService(pool);
        es.submit(() -> updatePointThrd1(pointDto));
        es.submit(() -> updatePointThrd3(pointDto));
        List<Double> expectedPointValues = new ArrayList<>();
        for(int i=0;i<2;i++) {
            Future<PointDTO> f = es.take();
            expectedPointValues.add(f.get().getValue());
        }
        Assert.assertEquals(2, expectedPointValues.size());
        Assert.assertTrue(expectedValues.containsAll(expectedPointValues));
        pool.shutdown();
    }

    @Test
    public void testAddConcurrent() throws ExecutionException, InterruptedException {
        PointDTO pointDto =  PointDTO.builder()
                                        .location(LocationEnum.EE)
                                        .measurementDay("2088-01-29")
                                        .value(10.11).build();
        addPoint(pointDto);
        List<Double> expectedValues = Arrays.asList(20.22, 30.33);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService es = new ExecutorCompletionService(pool);
        es.submit(() -> updatePointThrd1(pointDto));
        es.submit(() -> updatePointThrd3(pointDto));
        List<Double> expectedPointValues = new ArrayList<>();
        for(int i=0;i<2;i++) {
            Future<PointDTO> f = es.take();
            expectedPointValues.add(f.get().getValue());
        }
        Assert.assertEquals(2, expectedPointValues.size());
        Assert.assertTrue(expectedValues.containsAll(expectedPointValues));
        pool.shutdown();
    }

    @Test
    public void testAddFailure() throws ExecutionException, InterruptedException {
        PointDTO pointDto =  PointDTO.builder()
                .location(LocationEnum.EE)
                .measurementDay("2088-02-29")
                .value(10.11).build();
        addPoint(pointDto);
        List<Double> expectedValues = Arrays.asList(100.12, 300.45);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        ExecutorCompletionService es = new ExecutorCompletionService(pool);
        es.submit(() -> updatePointThrd1(pointDto));
        es.submit(() -> updatePointThrd3(pointDto));
        List<Double> expectedPointValues = new ArrayList<>();
        for(int i=0;i<2;i++) {
            Future<PointDTO> f = es.take();
            expectedPointValues.add(f.get().getValue());
        }
        Assert.assertNotEquals(1, expectedPointValues.size());
        Assert.assertFalse(expectedValues.containsAll(expectedPointValues));
        pool.shutdown();
    }

    private void addPoint(final PointDTO pointDTO) throws InterruptedException {
        pointService.addPoint(pointDTO);
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
        try {//some delay before adding
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
