package com.eesti.energia.point.service;

import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.dto.SummaryDTO;

import org.springframework.web.bind.annotation.RequestParam;

public interface PointService {

    public PointDTO addPoint(PointDTO pointDto) throws InterruptedException;

    public SummaryDTO pointsSummary();

    public SummaryDTO viewPointsPaginated(int offset, int limit);

    public void deletePoint(String id) throws InterruptedException;
}
