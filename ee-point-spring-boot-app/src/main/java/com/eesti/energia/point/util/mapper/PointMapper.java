package com.eesti.energia.point.util.mapper;

import java.util.List;

import com.eesti.energia.point.dto.PointDTO;
import com.eesti.energia.point.entity.Point;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PointMapper {

	//TestInfo mapEntityToInfo(TestEntity testEntity);

	@Mappings({
			@Mapping(target="measurementDay",expression = "java( com.eesti.energia.point.util.DateUtil.getMillisFromString(pointDTO.getMeasurementDay()))")
	})
	public Point mapDtoToEntity(PointDTO pointDTO);

	@Mappings({
			@Mapping(source = "id", target="id"),
			@Mapping(target="measurementDay",expression = "java( com.eesti.energia.point.util.DateUtil.getStringFromMillis(point.getMeasurementDay()))")
	})
	public PointDTO mapEntityToDto(Point point);

}
