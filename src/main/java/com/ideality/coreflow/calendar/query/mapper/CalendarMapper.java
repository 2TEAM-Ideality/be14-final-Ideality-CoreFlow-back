package com.ideality.coreflow.calendar.query.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;
import com.ideality.coreflow.calendar.query.dto.ResponseScheduleDTO;
import com.ideality.coreflow.calendar.query.dto.ScheduleDetailDTO;
import com.ideality.coreflow.calendar.query.dto.TodayScheduleDTO;

@Mapper
public interface CalendarMapper {
	List<ResponseScheduleDTO> selectAllPersonal(Long userId);

	ScheduleDetailDTO selectScheduleDetail(Map<String, Object> param);

	List<TodayScheduleDTO> selectTodayPersonal(Map<String, Object> param);

	FrequencyInfo selectRepeatRule(Long scheduleId);

	List<ResponseScheduleDTO> selectScheduleByMonth(Map<String, Object> param);
}
