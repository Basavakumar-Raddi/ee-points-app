package com.eesti.energia.point.service;

import com.eesti.energia.point.dao.OffsetLimitRequest;
import com.eesti.energia.point.dao.PointRepository;
import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.dto.SummaryDTO;
import com.eesti.energia.point.entity.Point;
import com.eesti.energia.point.util.LocationEnum;
import com.eesti.energia.point.util.mapper.PointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointServiceImpl implements PointService{

    @Inject
    private PointRepository pointRepository;

    @Autowired
    private PointMapper pointMapper;

    private ConcurrentReferenceHashMap<WeakMapObj,Lock> weakHashMap = new ConcurrentReferenceHashMap();
    Lock updateLock = new ReentrantLock();
    Lock addLock = new ReentrantLock();


    /**
     * saves the point to database and synchronizes the update and not add
     * @param pointDto
     * @return
     */
    @Override public PointDTO addPoint(PointDTO pointDto) throws InterruptedException {
        Point pointSaved = null;
        Point existingPoint = null;
        Point point = pointMapper.mapDtoToEntity(pointDto);
        WeakMapObj weakMapObj = new WeakMapObj(point.getMeasurementDay(),
                point.getLocation().getLocation(),point);
        weakHashMap.putIfAbsent(weakMapObj,new ReentrantLock());
        pointSaved = persistPoint(weakMapObj);
        //marking the weakMapObj as null so that when gc kicks in it can remove it form map
        weakMapObj = null;
        return pointMapper.mapEntityToDto(pointSaved);
    }

    private Point persistPoint(WeakMapObj weakMapObj) throws InterruptedException {
        Point pointSaved = null;
        Lock fetchedReentrantLock = weakHashMap.get(weakMapObj);
        try {
            if(fetchedReentrantLock.tryLock(10, TimeUnit.SECONDS)){
                //this fetch will help for concurrent updates, concurrent add requests, concurrent update and delete
                Point existingPoint = pointRepository.findByMeasurementDayAndLocation(
                        weakMapObj.getPoint().getMeasurementDay(), LocationEnum.valueOf(weakMapObj.getLocation()));
                //for handling delete in between update and delete
                if(existingPoint!=null) {
                    existingPoint.setValue(existingPoint.getValue() + weakMapObj.getPoint().getValue());
                    existingPoint.setLastModifiedBy("user");
                    existingPoint.setLastModifiedDate(new Date());
                    System.out.println("updating new point");
                } else {
                    existingPoint = weakMapObj.getPoint();
                    existingPoint.setCreatedBy("user");
                    existingPoint.setCreatedDate(new Date());
                    System.out.println("adding new point");
                }
                pointSaved = pointRepository.save(existingPoint);
            }
        } catch (InterruptedException e) {
            throw e;
        }finally{
            //release lock
            fetchedReentrantLock.unlock();
        }
        return pointSaved;
    }

    @Override public SummaryDTO pointsSummary() {
        List<Point> pointList = pointRepository.findAll();
        return getSummary(pointList);
    }

    /**
     * get the list of points from database
     * @param offset
     * @param limit
     * @return
     */
    @Override public SummaryDTO viewPointsPaginated(final int offset, final int limit) {
        List<String> sortAttr = Arrays.asList("measurementDay");
        Page<Point> pointsPage = pointRepository.findAll(
                new OffsetLimitRequest(offset, limit, new Sort(Sort.Direction.DESC, sortAttr)));
        List<Point> pointList = pointsPage.getContent();
        return getPointDtoList(pointList, pointsPage.getTotalElements());
    }

    /**
     * deletes the point
     * @param id
     */
    @Override public void deletePoint(String id) throws InterruptedException {
        Point existingPoint = pointRepository.getOne(id);
        WeakMapObj weakMapObj = new WeakMapObj(existingPoint.getMeasurementDay(),
                existingPoint.getLocation().getLocation(),existingPoint);
        //weakHashMap.put(weakMapObj,updateLock);
        weakHashMap.putIfAbsent(weakMapObj, new ReentrantLock());
        Lock fetchedReentrantLock = weakHashMap.get(weakMapObj);
        try {
            if(fetchedReentrantLock.tryLock(10, TimeUnit.SECONDS)){
                pointRepository.deleteById(id);
            }
        } catch (InterruptedException e) {
            throw e;
        }finally{
            //release lock
            fetchedReentrantLock.unlock();
        }
    }

    private SummaryDTO getPointDtoList(List<Point> pointList, Long totalRows){
        SummaryDTO summaryDTO = new SummaryDTO();
        List<PointDTO> pointDTOS = new ArrayList<>();
        for(Point point : pointList){
            pointDTOS.add(pointMapper.mapEntityToDto(point));
        }

        DoubleSummaryStatistics stats = pointDTOS.stream()
                .mapToDouble((x) -> x.getValue())
                .summaryStatistics();

        summaryDTO.setPointDTOS(pointDTOS);
        summaryDTO.setTotalRows(totalRows);
        return summaryDTO;
    }

    private SummaryDTO getSummary(List<Point> pointList){
        SummaryDTO summaryDTO = new SummaryDTO();
        List<PointDTO> pointDTOS = new ArrayList<>();
        for(Point point : pointList){
            pointDTOS.add(pointMapper.mapEntityToDto(point));
        }

        DoubleSummaryStatistics stats = pointDTOS.stream()
                .mapToDouble((x) -> x.getValue())
                .summaryStatistics();

        summaryDTO.setAverageValue(stats.getAverage());
        summaryDTO.setMaxValue(stats.getMax());
        summaryDTO.setMinValue(stats.getMin());
        summaryDTO.setTotalValue(stats.getSum());
        return summaryDTO;
    }
}
