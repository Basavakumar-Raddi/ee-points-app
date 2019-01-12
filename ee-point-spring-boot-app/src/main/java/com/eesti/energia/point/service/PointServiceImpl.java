package com.eesti.energia.point.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import javax.inject.Inject;

import com.eesti.energia.point.dao.OffsetLimitRequest;
import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.dto.SummaryDTO;
import com.eesti.energia.point.entity.Point;
import com.eesti.energia.point.dao.PointRepository;
import com.eesti.energia.point.util.DateUtil;
import com.eesti.energia.point.util.mapper.PointMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService{

    @Inject
    private PointRepository pointRepository;

    @Autowired
    private PointMapper pointMapper;
    Object updatePointLock = new Object();


    /**
     * saves the point to database and synchronizes the update and not add
     * @param pointDto
     * @return
     */
    @Override public PointDTO addPoint(PointDTO pointDto) {
        Point pointSaved = null;
        Point point = pointMapper.mapDtoToEntity(pointDto);

        Point existingPoint = pointRepository.findByMeasurementDayAndLocation(point.getMeasurementDay(), point.getLocation());

        if(existingPoint != null){
            synchronized(updatePointLock) {
                existingPoint.setValue(existingPoint.getValue() + point.getValue());
                existingPoint.setLastModifiedBy("user");
                existingPoint.setLastModifiedDate(new Date());
                pointSaved = pointRepository.save(existingPoint);
            }
        } else {
            point.setCreatedBy("user");
            point.setCreatedDate(new Date());
            pointSaved = pointRepository.save(point);
        }

        return pointMapper.mapEntityToDto(pointSaved);
    }

    @Override public SummaryDTO pointsSummary() {
        List<Point> pointList = pointRepository.findAll();
        /*List<PointDTO> response = ConversionUtil.convertJson(ConversionUtil.convertToJsonString(pointList),
                new TypeReference<List<PointDTO>>(){}); */
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
    @Override public void deletePoint(String id) {
        pointRepository.deleteById(id);
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
