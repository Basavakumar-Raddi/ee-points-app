package com.eesti.energia.point.test.controller;

import com.eesti.energia.point.controller.PointController;
import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.dto.SummaryDTO;
import com.eesti.energia.point.service.PointService;
import com.eesti.energia.point.util.LocationEnum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PointController.class, secure = false)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    public void viewPointsTest() throws Exception{
        SummaryDTO mockSummaryDTO = new SummaryDTO();
        mockSummaryDTO.setTotalValue(100.50);

        Mockito.when(pointService.viewPointsPaginated(ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt())).thenReturn(mockSummaryDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/points").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\":{\"averageValue\":null,\"minValue\":null,\"maxValue\":null,\"totalValue\":100.5,"
                + "\"pointDTOS\":null},\"success\":true,\"errorCode\":null,\"message\":\"Points fetched successfully\"}";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);

    }

    @Test
    public void addPointTest() throws Exception{
        PointDTO pointDTO = new PointDTO();
        pointDTO.setLocation(LocationEnum.EE);
        pointDTO.setValue(11.35);
        pointDTO.setMeasurementDay("2018-12-17");
        pointDTO.setId("4b423fb5-edd6-4255-b5cd-ab1f8d0e16cd");

        Mockito.when(pointService.addPoint(Mockito.any(PointDTO.class))).thenReturn(pointDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/points")
                .accept(MediaType.APPLICATION_JSON).content("{\"measurementDay\": \"2018-12-17\", \"location\": \"EE\", \"value\": 11.35} ")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\": {\"id\": \"4b423fb5-edd6-4255-b5cd-ab1f8d0e16cd\", \"measurementDay\": \"2018-12-17\", "
                + "\"location\": \"EE\", \"value\": 11.35}, \"success\": true, \"errorCode\": null, \"message\": \"Point added successfully\"}";
        Assert.assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void deletePointTest() throws Exception {
        Mockito.doNothing().when(pointService).deletePoint(Mockito.any(String.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/points?id=123");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\": null, \"success\": true, \"errorCode\": null, \"message\": \"Point deleted successfully\"}";
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);

    }

    @Test
    public void failViewPointsTest() throws Exception{
        SummaryDTO mockSummaryDTO = new SummaryDTO();
        mockSummaryDTO.setTotalValue(100.50);

        Mockito.when(pointService.viewPointsPaginated(ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt())).thenReturn(mockSummaryDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/points").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\":{\"averageValue\":null,\"minValue\":null,\"maxValue\":null,\"totalValue\":8.8,"
                + "\"pointDTOS\":null},\"success\":true,\"errorCode\":null,\"message\":\"Points not fetched \"}";
        JSONAssert.assertNotEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void failAddPointTest() throws Exception{
        PointDTO pointDTO = new PointDTO();
        pointDTO.setLocation(LocationEnum.EE);
        pointDTO.setValue(11.35);
        pointDTO.setMeasurementDay("2018-12-17");
        pointDTO.setId("4b423fb5-edd6-4255-b5cd-ab1f8d0e16cd");

        Mockito.when(pointService.addPoint(Mockito.any(PointDTO.class))).thenReturn(pointDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/points")
                .accept(MediaType.APPLICATION_JSON).content("{\"measurementDay\": \"2018-12-17\", \"location\": \"EE\", \"value\": 11.35} ")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\": {\"id\": \"12345\", \"measurementDay\": \"2018-05-12\", "
                + "\"location\": \"FI\", \"value\": 67.35}, \"success\": true, \"errorCode\": null, \"message\": \"Point aaaaaa\"}";

        JSONAssert.assertNotEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void failDeletePointTest() throws Exception {
        Mockito.doNothing().when(pointService).deletePoint(Mockito.any(String.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/points?id=123");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"response\": null, \"success\": true, \"errorCode\": null, \"message\": \"Point added \"}";
        JSONAssert.assertNotEquals(expected, result.getResponse()
                .getContentAsString(), false);

    }

}
