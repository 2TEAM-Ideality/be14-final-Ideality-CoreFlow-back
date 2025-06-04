package com.ideality.coreflow.calendar.query.mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;

@Mapper
public interface CalendarMapper {
	Optional<List<ResponseScheduleDTO>> selectAllPersonal(Long userId);

	Optional<ResponseScheduleDTO> selectPersonalDetail(Map<String, Object> param);

	Optional<List<ResponseScheduleDTO>> seelctAllDeptSchedule(Long deptId);
}
