package com.eesti.energia.point.util;

import java.io.IOException;

import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.entity.Point;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ConversionUtil {

    public static Point convertToPoint(PointDTO pointDTO) {
        Point point = new Point();
        ObjectMapper mapper = new ObjectMapper();
        point = mapper.convertValue(pointDTO, Point.class);
        return point;
    }

    public static <T> T convertJson(String jsonString, Class<T> clazz) { //throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        T resultObject = null;
        try {
            resultObject = objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error("error while converting to object from json sring",e);
            //LOGGER.error("An error occurred while converting json {} to object {}.", jsonString, clazz);
            /*SystemException.newSystemException("",
                    new Object[] { String.format("An error occurred while converting json %s.", jsonString) });*/
        }
        return resultObject;
    }

    /**
     * This method would convert any object to JSON String
     *
     * @param data
     * @return
     */
    public static <T> String convertToJsonString(T data) { //throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            if (data != null) {
                jsonStr = objectMapper.writeValueAsString(data);
            }
        } catch (IOException e) {
            log.error("error while converting to json sring",e);
            /*SystemException.newSystemException("", new Object[] {
                    String.format("An error occurred while converting %s to json string.", data.getClass()) });*/
        }
        return jsonStr;
    }

    public static <T> T convertJson(String jsonString, TypeReference typeRef){ //} throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        // objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        // objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        T resultObject = null;
        try {
            resultObject = objectMapper.readValue(jsonString, typeRef);
        } catch (IOException e) {
            log.error("IoException occurred",e);
            /*SystemException.newSystemException(BusinessExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting json to %s.", typeRef) });*/
        }
        return resultObject;
    }
}
