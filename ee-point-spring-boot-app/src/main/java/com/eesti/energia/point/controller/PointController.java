package com.eesti.energia.point.controller;

import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.dto.SummaryDTO;
import com.eesti.energia.point.service.PointService;
import com.eesti.energia.point.util.ConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/api/v1/points")
@Slf4j
public class PointController {

    @Inject
    private PointService pointService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PointDTO>> addPoint(@RequestBody String pointRequest) {
        ApiResponse<PointDTO> response = new ApiResponse<>();
        try {
            PointDTO pointDto = ConversionUtil.convertJson(pointRequest, PointDTO.class);
            response.setResponse(pointService.addPoint(pointDto));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Point added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("error occurred during add point", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("error occurred while adding the point");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SummaryDTO>> viewPoints(
            @RequestParam( value = "offset",defaultValue = "0", required = false) int offset,
            @RequestParam( value="limit", defaultValue = "5000", required = false ) int limit) {
        ApiResponse<SummaryDTO> response = new ApiResponse<>();
        try {
            response.setResponse(pointService.viewPointsPaginated(offset,limit));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Points fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("error occurred while fetch", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("error occurred while fetching the points");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<PointDTO>> deletePoint(@RequestParam String id) {
        ApiResponse<PointDTO> response = new ApiResponse<>();
        try {
            pointService.deletePoint(id);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Point deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("error occurred during delete point", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("error occurred while deleting the point");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<SummaryDTO>> pointsSummary() {
        ApiResponse<SummaryDTO> response = new ApiResponse<>();
        try {
            response.setResponse(pointService.pointsSummary());
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Summary fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("error occurred while summary fetch", e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("error occurred while fetching the summary");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}